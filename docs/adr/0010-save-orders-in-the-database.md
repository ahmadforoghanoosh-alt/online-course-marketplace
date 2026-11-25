# ADR-0010: Save paid orders in the database with an Observer

- Status: accepted
- Date: 2026-11-25
- Deciders: Forogh Anoosh (Maintainer), Shabir Mohibi (Architect)

## Context
ADR-0004 saved the courses in SQLite, but orders and "My courses" were still kept in memory, so after a restart a
student lost every course he bought, and the next order started again at ORD-1. For the final defence we must show
a connected database, so the purchases must be saved too. We did not want to change `CheckoutService`, which is
already tested and is the subject of our Observer (register row 3).

## Options considered
1. Call a repository inside `CheckoutService.checkout()`. Bad: checkout gets one more job and one more dependency,
   and every test of checkout must think about saving.
2. A new Observer: `OrderHistoryService implements OrderListener` saves every paid order through an `OrderRepository`
   port. Good: `CheckoutService` does not change, it only gets one more listener in `App` (Open/Closed). Failed
   payments are not saved, because listeners are only told about paid orders.
3. Save the whole `Student` object. Bad: we would mix the student, his cart and his orders in one table.

## Decision
We chose option 2. The database gets two new tables: `orders` (reference, student, total, paid_at) and
`order_items` (one row per bought course). `order_items` keeps a copy of the title, instructor and price, like a real
receipt, so old orders stay correct if a course is changed or deleted later (US-11); for the same reason `course_id`
is not a foreign key to `courses`. An order and its items are saved in one transaction. At start, `App` restores the
student's courses from the saved orders and continues the order numbers (`CheckoutService(firstOrderNumber)`).
`GET /orders` shows the history. With `--memory`, `InMemoryOrderRepository` is used.

## Consequences
Good: orders and "My courses" survive a restart; the third listener shows why we chose Observer; services and UI only
know the `OrderRepository` interface, so `ArchitectureTest` still passes.
Bad / cost: like `SqliteCourseRepository`, `SqliteOrderRepository` has no unit test (no database in tests); it was
checked by hand: buy, restart, the order and the course are still there and the next order is ORD-2.
Completed courses (for certificates) are still kept in memory.
