# ADR-0004: Save courses in a SQLite database (second Repository)

- Status: accepted
- Date: 2026-11-25
- Deciders: Forogh Anoosh (Maintainer), Shabir Mohibi (Architect)

## Context
Until now `InMemoryCourseRepository` kept the courses in a `Map`. When the app stops, every course
an instructor added is lost. The capstone asks for a second Repository implementation (a file, or SQLite
through JDBC) that we can switch to without changing the service layer.
`CatalogService` and `CartService` only use the `CourseRepository` interface from the domain (ADR-0001),
so the switch can happen in one place: `App`.

## Options considered
1. Text or CSV file. Good: no library. Bad: we must write our own parsing and id counter, and it is easy to break the file.
2. SQLite through JDBC. Good: a real database in one file (`marketplace.db`), SQL with `PreparedStatement`, the id comes from `AUTOINCREMENT`, no server to install. Bad: one new dependency (`org.xerial:sqlite-jdbc`).
3. A database server (PostgreSQL, MySQL). Bad: every reviewer must install and start a server, so "clone and run one command" breaks.

## Decision
We chose option 2. `SqliteCourseRepository` (infra) implements `CourseRepository` with plain JDBC and
`PreparedStatement` (no SQL injection). `App.chooseRepository()` is the only place that picks the storage:
SQLite by default, `--memory` for the old in-memory version. Sample courses are added only when the table is empty.
The `.db` file is in `.gitignore`.

## Consequences
Good: courses stay after a restart; services, UI and their tests did not change (Open/Closed, Dependency Inversion);
`ArchitectureTest` still passes because only infra uses `java.sql`.
Bad / cost: `SqliteCourseRepository` has no unit test, because the rules say no test may depend on a database;
the services are tested with `FakeCourseRepository` and `InMemoryCourseRepository` instead, and the class was
checked by hand (add a course, restart, the course is still there). Orders and enrollments are still in memory.
