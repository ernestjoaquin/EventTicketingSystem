package com.eventticketing.util;

import com.eventticketing.model.User;

import java.io.*;

/**
 * Concrete SessionStore implementation that persists the logged-in user
 * to a serialized file (session.dat) using Java Serialization
 * (ObjectOutputStream / ObjectInputStream).
 *
 * This is the class responsible for the "create / use / delete session.dat"
 * requirement. It has exactly one job — reading and writing the session
 * file — which is also an example of the Single Responsibility Principle:
 * SessionManager coordinates session state, while FileSessionStore owns the
 * detail of how that state is stored on disk.
 */
public class FileSessionStore implements SessionStore {

    private static final String SESSION_FILE = "session.dat";

    @Override
    public void save(User user) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SESSION_FILE))) {
            oos.writeObject(user);
        } catch (IOException e) {
            System.err.println("Failed to create session file: " + e.getMessage());
        }
    }

    @Override
    public User load() {
        File file = new File(SESSION_FILE);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof User) {
                return (User) obj;
            }
            return null;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to read session file: " + e.getMessage());
            // Corrupt/unreadable session file — remove it so it doesn't keep failing.
            clear();
            return null;
        }
    }

    @Override
    public void clear() {
        File file = new File(SESSION_FILE);
        if (file.exists() && !file.delete()) {
            System.err.println("Failed to delete session file: " + file.getAbsolutePath());
        }
    }

    @Override
    public boolean exists() {
        return new File(SESSION_FILE).exists();
    }
}
