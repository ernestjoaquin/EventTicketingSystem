package com.eventticketing.util;

import com.eventticketing.model.User;

/** Holds the currently logged-in user and shopping cart for the app session. */
public class SessionManager {

    private static SessionManager instance;
    private User currentUser;
    private final java.util.List<com.eventticketing.model.Seat> cart = new java.util.ArrayList<>();
    private com.eventticketing.model.Event cartEvent; // seats in cart must belong to one event at a time

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User user) { this.currentUser = user; }
    public void logout() { currentUser = null; cart.clear(); cartEvent = null; }

    public java.util.List<com.eventticketing.model.Seat> getCart() { return cart; }

    public com.eventticketing.model.Event getCartEvent() { return cartEvent; }

    public void addToCart(com.eventticketing.model.Event event, com.eventticketing.model.Seat seat) {
        if (cartEvent != null && cartEvent.getEventId() != event.getEventId()) {
            cart.clear(); // only one event's tickets in the cart at a time
        }
        cartEvent = event;
        if (!cart.contains(seat)) cart.add(seat);
    }

    public void removeFromCart(com.eventticketing.model.Seat seat) {
        cart.remove(seat);
        if (cart.isEmpty()) cartEvent = null;
    }

    public void clearCart() { cart.clear(); cartEvent = null; }

    public double getCartTotal() {
        return cart.stream().mapToDouble(com.eventticketing.model.Seat::getPrice).sum();
    }
}
