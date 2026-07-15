# Event Ticketing System — JavaFX (No Database Version)

A desktop implementation of the Event Ticketing System from your UML diagrams
(Use Case, Activity, Sequence, and Class diagrams), built with **JavaFX** for
the UI. This version has **no database** — all data (users, events, venues,
categories, seats, bookings, payments) is stored in memory while the app is
running, using a `DataStore` class instead of MySQL/XAMPP.

> This is meant as an earlier-stage submission before the database is added.
> Data resets every time you restart the app — that's expected, since there's
> no persistent storage yet.

## What's implemented

| Diagram | Implemented as |
|---|---|
| Use Case Diagram | Login/Register, Browse/Search Events, View Details, Add to Cart, Make Payment (Process Payment include), View My Tickets, Cancel Ticket (extend), Admin: Manage Events/Categories/Users, View Reports |
| Activity Diagram (Book Tickets) | `BookingDAO.checkout()` — creates a **PENDING** booking, reserves seats, simulates the payment gateway (`Payment.processPayment()`), then confirms the booking, exactly following your swimlane flow |
| Sequence Diagram (Book Tickets) | UI (`CheckoutController`) → `BookingDAO` (BookingService) → `Payment` model (PaymentGateway) → `DataStore` (in-memory), then back to `MyTicketsController` (View My Tickets / e-ticket) |
| Class Diagram | `User` (abstract) → `Customer`, `Admin`; `Event`, `Category`, `Venue`, `Booking`, `BookingItem`, `Payment`, `Seat` — mirrored 1:1 in `model/` |

## Project structure

```
EventTicketingSystem/
├── pom.xml                          # Maven build file (JavaFX only)
├── src/main/java/com/eventticketing/
│   ├── Main.java                    # JavaFX entry point
│   ├── datastore/DataStore.java     # In-memory "database" (replaces MySQL for now)
│   ├── model/                       # User, Customer, Admin, Event, Category, Venue,
│   │                                 # Booking, BookingItem, Payment, Seat
│   ├── dao/                         # UserDAO, EventDAO, CategoryDAO, VenueDAO, BookingDAO
│   │                                 # (same method signatures as before, now backed by DataStore)
│   ├── controller/                  # One controller per screen (JavaFX FXML controllers)
│   └── util/                        # SceneManager, SessionManager, PasswordUtil
└── src/main/resources/com/eventticketing/
    ├── view/*.fxml                  # Login, Register, BrowseEvents, EventDetails,
    │                                 # Cart, Checkout, MyTickets, AdminDashboard
    └── css/style.css                # App styling
```

## Setup

### 1. Open the project
Import the folder as a **Maven project** in your IDE (IntelliJ IDEA, Eclipse, or
NetBeans all support this directly via `pom.xml`). Maven will download the
JavaFX dependencies automatically. No XAMPP, MySQL, or database import needed.

Requires **JDK 17+**.

### 2. Run the app
- From your IDE: run `Main.java`.
- From the command line (with Maven installed):
  ```
  mvn clean javafx:run
  ```

### 3. Log in
- **Admin:** `admin@ticketing.com` / `admin123`
- **Customer:** click "Create one here" on the login screen to register a new account.

The app starts pre-loaded with the same sample data the database version used:
3 sample events (Summer Music Fest, Tech Innovators Conference, Championship
Basketball Finals), 4 categories, 3 venues, and the admin account above.

## How the "no database" part works

Every DAO (`UserDAO`, `EventDAO`, `CategoryDAO`, `VenueDAO`, `BookingDAO`) keeps
the **exact same public methods** it had before — `register()`, `login()`,
`getAllEvents()`, `checkout()`, `cancelBooking()`, etc. Internally, instead of
opening a JDBC connection and running SQL, each method reads from and writes to
plain Java lists held in `DataStore` (a singleton). The controllers, models,
FXML, and CSS were not touched at all.

When you're ready to add the real database back:
1. Add back `db/DBConnection.java` and the MySQL dependency in `pom.xml`.
2. Import a `schema.sql` (in phpMyAdmin, or recreate it).
3. Swap the DAO method bodies back to JDBC/SQL — nothing else in the project
   needs to change, since every other class only ever talks to the DAOs.

## Notes & things you may want to extend

- **Payment gateway**: `Payment.processPayment()` currently *simulates* a
  successful payment. To integrate a real gateway (Stripe, PayMongo, PayPal,
  etc.), replace that method's body with an actual API call and pass through
  the result.
- **Email**: The e-ticket "email" is currently just a confirmation dialog.
  To send real emails, add a library like Jakarta Mail and call it from
  `CheckoutController.handlePay()` after a successful booking.
- **Concurrency**: `BookingDAO.checkout()` is `synchronized` to prevent two
  customers from double-booking the same seat while the app is running.
- **Passwords** are hashed with SHA-256 before being stored (see
  `util/PasswordUtil.java`). For production use, prefer bcrypt/Argon2.
- **Persistence**: since everything lives in `DataStore`, all data is lost
  when the app closes. That's expected for this version.
