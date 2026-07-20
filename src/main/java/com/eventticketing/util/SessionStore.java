package com.eventticketing.util;

import com.eventticketing.model.User;

/**
 * Abstraction for persisting the logged-in user's session.
 *
 * SessionManager (a high-level module) depends on this interface rather
 * than on a concrete storage mechanism (a low-level module) — this is the
 * Dependency Inversion Principle (DIP) in practice. FileSessionStore is the
 * default implementation, backed by Java Serialization, but SessionManager
 * itself has no knowledge of files, streams, or serialization. A different
 * implementation (e.g. an in-memory store for unit tests, or a database-backed
 * store) could be swapped in without changing SessionManager at all.
 */
public interface SessionStore {

    /** Persists the given user as the active session. */
    void save(User user);

    /** Loads the previously persisted user, or null if no valid session exists. */
    User load();

    /** Removes any persisted session. */
    void clear();

    /** Returns true if a persisted session currently exists. */
    boolean exists();
}
