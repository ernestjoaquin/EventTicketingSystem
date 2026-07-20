# Event Ticketing System — JavaFX + MySQL (XAMPP)

A desktop implementation of the Event Ticketing System from your UML diagrams
(Use Case, Activity, Sequence, and Class diagrams), built with **JavaFX** for
the UI and **MySQL/MariaDB (via XAMPP)** for the database.

## What's implemented

| Diagram | Implemented as |
|---|---|
| Use Case Diagram | Login/Register, Browse/Search Events, View Details, Add to Cart, Make Payment (Process Payment include), View My Tickets, Cancel Ticket (extend), Admin: Manage Events/Categories/Users, View Reports |
| Activity Diagram (Book Tickets) | `BookingDAO.checkout()` — creates a **PENDING** booking, reserves seats, simulates the payment gateway (`Payment.processPayment()`), then confirms the booking, exactly following your swimlane flow |
| Sequence Diagram (Book Tickets) | UI (`CheckoutController`) → `BookingDAO` (BookingService) → `Payment` model (PaymentGateway) → DB, then back to `MyTicketsController` (View My Tickets / e-ticket) |
| Class Diagram | `User` (abstract) → `Customer`, `Admin`; `Event`, `Category`, `Venue`, `Booking`, `BookingItem`, `Payment`, `Seat` — mirrored 1:1 in `model/` and `database/schema.sql` |

## Project structure

```
EventTicketingSystem/
├── pom.xml                          # Maven build file (JavaFX + MySQL connector)
├── database/
│   └── schema.sql                   # Full MySQL schema + seed data for phpMyAdmin
├── src/main/java/com/eventticketing/
│   ├── Main.java                    # JavaFX entry point
│   ├── db/DBConnection.java         # JDBC connection to XAMPP MySQL
│   ├── model/                       # User, Customer, Admin, Event, Category, Venue,
│   │                                 # Booking, BookingItem, Payment, Seat
│   ├── dao/                         # UserDAO, EventDAO, CategoryDAO, VenueDAO, BookingDAO
│   ├── controller/                  # One controller per screen (JavaFX FXML controllers)
│   └── util/                        # SceneManager, SessionManager, SessionStore,
│                                     # FileSessionStore, PasswordUtil
└── src/main/resources/com/eventticketing/
    ├── view/*.fxml                  # Login, Register, BrowseEvents, EventDetails,
    │                                 # Cart, Checkout, MyTickets, AdminDashboard
    └── css/style.css                # App styling
```

## Session Management (Java Serialization)

On a successful login, the app no longer just keeps the user in memory — it
creates a **serialized session file (`session.dat`)** containing the
logged-in `User` object, and uses that file to validate and maintain the
session as the user navigates between screens.

**How it works:**

- **Creation:** `LoginController.handleLogin()` calls
  `SessionManager.getInstance().login(user)`. This sets the in-memory
  current user *and* delegates to `FileSessionStore.save(user)`, which
  writes the `User` object to `session.dat` using an
  `ObjectOutputStream` (`User`, `Customer`, and `Admin` all implement
  `Serializable`).
- **Usage / validation:** `SceneManager.switchTo(...)` is the single choke
  point every screen transition passes through. Before loading any screen
  other than Login/Register, it calls `SessionManager.isSessionValid()`,
  which checks both that a user is loaded in memory *and* that
  `session.dat` still exists on disk (via `FileSessionStore.exists()`). If
  either check fails, the user is redirected back to the login screen
  instead of being allowed to proceed. On application startup, `Main.java`
  also calls `SessionManager.restoreSession()`, which reads `session.dat`
  (via `ObjectInputStream`) so a user who closed the app without logging
  out is taken straight to their dashboard instead of the login screen.
- **Deletion:** Every logout handler (`CartController`,
  `BrowseEventsController`, `AdminDashboardController`,
  `EventDetailsController`, `MyTicketsController`) calls
  `SessionManager.logout()`, which clears the in-memory user/cart **and**
  deletes `session.dat` via `FileSessionStore.clear()`, then redirects to
  the Login screen.

**Files involved:** `util/SessionManager.java`, `util/SessionStore.java`
(interface), `util/FileSessionStore.java` (serialization implementation),
`model/User.java` (`implements Serializable`).

## SOLID Principles Applied

### 1. Single Responsibility Principle (SRP)
Each class has exactly one reason to change:
- **DAO classes** (`UserDAO`, `EventDAO`, `BookingDAO`, `CategoryDAO`,
  `VenueDAO`) are solely responsible for talking to the database (SQL,
  `Connection`/`PreparedStatement`). They know nothing about JavaFX.
- **Controller classes** (`LoginController`, `CheckoutController`, etc.)
  are solely responsible for UI logic — reading form input, calling a DAO
  or the SessionManager, and updating the screen.
- **`FileSessionStore`** is solely responsible for how the session is
  physically persisted (serialize/deserialize/delete `session.dat`), while
  **`SessionManager`** is solely responsible for *session state* (who's
  logged in, what's in the cart) — it doesn't know or care how that state
  gets saved to disk.

  **Benefit:** Changing the database schema only touches DAO classes;
  changing how a screen looks only touches its controller; changing how
  sessions are stored (e.g. switching to encrypted files, or a database
  table) only touches `FileSessionStore` — nothing else in the app needs
  to change.

### 2. Dependency Inversion Principle (DIP)
`SessionManager` (a high-level class that coordinates session behavior)
does **not** depend on the concrete `FileSessionStore` class directly in
its public contract — it depends on the `SessionStore` **interface**,
which declares `save()`, `load()`, `clear()`, and `exists()`.
`FileSessionStore` is just one implementation of that interface, using
Java Serialization under the hood.

**Benefit:** `SessionManager` has zero knowledge of files, streams, or
serialization — it only knows "a `SessionStore` can save/load/clear a
user." This means the storage mechanism can be swapped (e.g. an
in-memory `SessionStore` for unit testing, or a database-backed one for a
future multi-device version) without changing a single line of
`SessionManager`, `LoginController`, or any other class that depends on
session state.

