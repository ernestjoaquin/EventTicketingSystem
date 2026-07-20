package com.eventticketing.util;

import com.eventticketing.model.User;

/**
 * Holds the currently logged-in user and shopping cart for the app session,
 * and coordinates persisting that session to disk via a {@link SessionStore}.
 *
 * SessionManager depends on the SessionStore *interface*, not on
 * FileSessionStore directly (Dependency Inversion Principle). It only asks
 * the store to save/load/clear a User — it never touches files or streams
 * itself. That detail lives entirely in FileSessionStore.
 */
public class SessionManager {

    private static SessionManager instance;

    private final SessionStore sessionStore;

    private User currentUser;
    private final java.util.List<com.eventticketing.model.Seat> cart = new java.util.ArrayList<>();
    private com.eventticketing.model.Event cartEvent; // seats in cart must belong to one event at a time

    private SessionManager() {
        this.sessionStore = new FileSessionStore();
    }

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    /**
     * Call this after a successful login. Sets the in-memory current user
     * AND writes the serialized session.dat file so the session can be
     * validated/restored as the user navigates (or reopens) the app.
     */
    public void login(User user) {
        this.currentUser = user;
        sessionStore.save(user);
    }

    public User getCurrentUser() { return currentUser; }

    /**
     * Kept for backward compatibility with any code that only needs the
     * in-memory user set (e.g. tests). Prefer login(user) in real flows so
     * the serialized session file stays in sync.
     */
    public void setCurrentUser(User user) { this.currentUser = user; }

    /**
     * True only if there is a user in memory AND a valid session.dat file
     * backing it. Controllers/SceneManager use this to validate the session
     * before letting the user navigate to a protected screen.
     */
    public boolean isSessionValid() {
        return currentUser != null && sessionStore.exists();
    }

    /**
     * Attempts to restore a session from session.dat (e.g. on app startup).
     * Returns true and populates currentUser if a valid session was found.
     */
    public boolean restoreSession() {
        User restored = sessionStore.load();
        if (restored != null) {
            this.currentUser = restored;
            return true;
        }
        return false;
    }

    /** Logs the user out: clears in-memory state AND deletes session.dat. */
    public void logout() {
        currentUser = null;
        cart.clear();
        cartEvent = null;
        sessionStore.clear();
    }

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
