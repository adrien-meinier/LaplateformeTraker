# La Plateforme Tracker

### Overview

La Plateforme Tracker is a desktop student management application built with Java and JavaFX. It allows teachers or administrators to manage a class roster, track individual grades and averages, and export reports — all backed by a local PostgreSQL database.

Built to learn and illustrate:
- Java 21 and JavaFX 21, compiled and packaged with Maven
- A Model-View-Controller architecture with a clean separation between DB access (DAO layer), business logic (controllers), and UI (views)
- Object-Oriented Design in Java: encapsulation, model classes, DAO pattern, singleton cache
- Unit testing and code coverage with JUnit 5 and JaCoCo reports

---

### Stack, Dependencies and Installation

**Requirements**
- Java 21
- Maven 3.8+
- PostgreSQL 14+ with `pg_dump` available on `PATH`

**Dependencies** (managed via `pom.xml`)

| Library | Version | Purpose |
|---|---|---|
| JavaFX Controls / FXML / Graphics | 21.0.2 | Desktop UI framework |
| PostgreSQL JDBC | 42.7.3 | Database connectivity |
| BCrypt | 0.10.2 | Password hashing |
| Apache Commons CSV | 1.10.0 | CSV import/export |
| Jackson Databind | 2.16.1 | JSON serialisation |
| JUnit Jupiter | 5.10.2 | Unit testing |
| JaCoCo | 0.8.11 | Code coverage reports |

**Setup**

1. Clone the repository and open the `demo/` folder.

2. Create the database:
```sql
CREATE DATABASE laplateformetracker;
```

3. Configure credentials in `DatabaseInitializer.java` if needed (default: `postgres` / `root` on `localhost:5432`).

4. Build and run:
```bash
mvn clean javafx:run
```

5. Generate coverage report:
```bash
mvn verify
# Report generated at: target/site/jacoco/index.html
```

---

### Features

**Authentication**
- Secure registration and login with BCrypt password hashing and a per-user salt
- Admin and standard user roles
- Password reset flow

**Student management**
- Full CRUD: add, edit, and delete students
- Input validation: name format, age range, future date rejection
- Duplicate detection: blocks identical first name + last name + birth date combinations
- Search and filter by name or birth date

**Grade management**
- Add and edit grades (0–20) per student and subject
- Automatic average calculation via a PostgreSQL trigger on the `grades` table
- Cascade delete: removing a student removes all their grades

**Statistics dashboard**
- Total student count, overall class average and top student (major de promo)
- Demographic breakdown panel (visual placeholder)

**Export and import**
- Export the full class roster with grades to CSV
- Import students from a CSV file
- Generate an individual student report card (bulletin) as CSV or PDF
- Manual and scheduled SQL dump backups via `pg_dump`

**Resilience**
- `InMemoryCache` singleton: on DB connection failure, all read operations fall back to the last known in-memory snapshot automatically
- `AutoBackupService`: background daemon thread that refreshes the cache and writes a timestamped SQL dump every N minutes

**Extras**
- Collapsible chatbot panel with keyword-based responses
- Consistent dark-themed UI via a centralised `StyleFactory`

---

### Dev Difficulties

**Java and its ecosystem** : Java itself was a learning curve, but the real challenge was configuration: aligning JDK versions, JavaFX module paths, and Maven settings across three different machines. The `pom.xml` became both the most important and most frustrating file in the project.

**PostgreSQL + JDBC** : Connecting PostgreSQL to Java via JDBC is significantly less intuitive than Python's `mysql-connector`. Understanding `PreparedStatement` lifecycles, result set handling, and properly closing connections (hence the `DatabaseConnection` wrapper class).

**Testing** : JUnit 5 and JaCoCo work very well for model classes and DAOs. Controllers are harder: testing JavaFX controllers requires a running FX thread, which means `TestFX` and headless mode configuration. View tests are even more constrained. Mockito would solve a lot of this, but Mockito's byte-buddy agent has compatibility issues with Java 21+, making proper mock-based controller tests difficult to set up reliably in this environment.

---

### Future Improvements

- **Cloud database** — migrate from a local PostgreSQL instance to a hosted solution (Render) so the app can be used across machines without manual DB setup
- **Cleaner project structure** — reorganise packages and split the `controller/` layer more clearly between business logic and JavaFX event handlers
- **Better test coverage on controllers and views** — revisit once Java LTS stabilises Mockito compatibility, and explore TestFX for proper FX thread testing

---

### Authors

Cécilia Perana, Adrien Meinier, Nelson Grac-Aubert

A project for **La Plateforme_ Marseille**
