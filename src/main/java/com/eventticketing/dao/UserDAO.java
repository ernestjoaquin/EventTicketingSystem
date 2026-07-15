package com.eventticketing.dao;

import com.eventticketing.datastore.DataStore;
import com.eventticketing.model.Admin;
import com.eventticketing.model.Customer;
import com.eventticketing.model.User;
import com.eventticketing.util.PasswordUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private final DataStore store = DataStore.getInstance();

    /** Registers a new customer. Returns the generated user id, or -1 on failure (e.g. duplicate email). */
    public int register(String name, String email, String phone, String plainPassword) {
        boolean duplicate = store.users.stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
        if (duplicate) {
            System.err.println("Email already registered.");
            return -1;
        }
        Customer customer = new Customer(store.userIdSeq.incrementAndGet(), name, email, phone,
                PasswordUtil.hash(plainPassword), LocalDateTime.now());
        store.users.add(customer);
        return customer.getUserId();
    }

    /** Authenticates a user by email/password. Returns a Customer or Admin instance, or null if invalid. */
    public User login(String email, String plainPassword) {
        for (User u : store.users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                if (PasswordUtil.verify(plainPassword, u.getPassword())) {
                    return u;
                }
                return null;
            }
        }
        return null;
    }

    public boolean updateProfile(int userId, String name, String phone) {
        for (User u : store.users) {
            if (u.getUserId() == userId) {
                u.setName(name);
                u.setPhone(phone);
                return true;
            }
        }
        return false;
    }

    public List<User> getAllCustomers() {
        List<User> list = new ArrayList<>();
        for (User u : store.users) {
            if ("CUSTOMER".equalsIgnoreCase(u.getRole())) list.add(u);
        }
        list.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        return list;
    }

    public boolean deleteUser(int userId) {
        return store.users.removeIf(u -> u.getUserId() == userId);
    }
}
