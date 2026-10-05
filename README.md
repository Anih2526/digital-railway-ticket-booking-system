# Digital Railway Ticket Booking System

Beginner-friendly B.Tech 2nd-year Java web application for railway reservation using Servlets, JSP, Core Java, JDBC, MySQL and Apache Tomcat.

## Project scope

### User Portal
- Registration and login
- Logout to the home page
- Station autocomplete while typing
- Reverse From/To button
- Journey date picker and date strip
- Optional train number/name search
- Train-only search results inspired by the supplied Where Is My Train screenshot
- Shows journey date, departure, arrival, duration, fare and available seats
- Sort by departure, arrival, duration, fare and available seats
- Interactive coach/seat selection
- Concurrent booking protection using Java synchronization and MySQL row locks
- Ticket confirmation
- Simulated payment confirmation
- Booking history

### Admin Portal — administrator controls the data used by the user portal
- Add/update stations
- Add/update routes
- Add/update trains and capacity
- Add/update schedules, train, route, date, departure, arrival, fare and status
- Add coaches and seat capacity
- Monitor bookings and revenue
- View reports and activity logs

## Deliberately not included
The supplied PDF lists online payment gateways/Razorpay/UPI, automatic waitlist, email ticket delivery and dynamic pricing under Future Scope. These are not implemented. Bus results, live train GPS/status, free-cancellation offers, PNR functions and other unrelated Where Is My Train features are also not implemented.

## Technologies
- HTML5, CSS3, JavaScript
- AJAX/Fetch API
- JSP
- Java Servlets
- Core Java / JDK 17+
- MySQL + JDBC
- Apache Tomcat ZIP
- Maven

## Laptop setup used by this project
- Windows
- JDK 17+
- Maven
- MySQL Server 8+ / Windows service `MySQL80`
- Apache Tomcat ZIP at `C:\apache-tomcat-11.0.26`
- VS Code

Change the Tomcat path at the top of `run.ps1` if your Tomcat folder is different.

## Database setup

### Fresh database
1. Run `database/schema.sql` in MySQL Workbench.
2. Run `database/sample_data.sql`.

### Existing database from the earlier project
Run `database/migrate.sql` once. It only adds the new schedule fare column if it is missing and gives old schedules a default fare of 100.00. It does not delete your existing bookings.

## One-command startup

Open the project folder in VS Code and run:

```powershell
.\run.ps1
```

If PowerShell execution policy blocks scripts on your Windows machine:

```powershell
powershell -ExecutionPolicy Bypass -File .\run.ps1
```

The script starts MySQL when needed, asks for the database password on first run, builds `railway.war`, deploys it to the ZIP-based Tomcat installation, starts Tomcat, waits for port 8080 and opens the application.

## Stop

```powershell
.\stop.ps1
```

## Demo accounts

User: `user@gmail.com` / `1234`

Admin: `admin@gmail.com` / `1234`

## Notes for the student

The code intentionally stays simple and uses no heavy application framework.
