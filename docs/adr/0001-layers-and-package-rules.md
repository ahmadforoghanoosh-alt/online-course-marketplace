# ADR-0001: Layers and package rules

- Status: accepted
- Date: 2026-11-18
- Deciders: Shabir Mohibi (Architect), Anoosh Forogh (Maintainer)

## Context
The Online Course Marketplace will grow every milestone: catalogue, cart, payments,
discounts and certificates. If the console code, the business rules and the storage
are mixed in one place, every change can break something else, and we can not test
the business rules without the console or the storage. Later (M4) we must also switch
the repository from memory to a file or SQLite without changing the services.

## Options considered
1. One package with all classes. Good: fewer files at the start. Bad: everything
   depends on everything, hard to test, and changing the storage touches the services.
2. Four layers `domain`, `service`, `infra`, `ui` under `kz.kaznaru.capstone`, with
   dependencies pointing inward. Good: rules can be tested alone, storage and vendors
   can change behind interfaces. Bad: more packages and interfaces.

## Decision
We chose option 2.
- `domain`: entities (Course, Student, Cart, Order) and the interfaces (ports) that other
  layers implement, such as `CourseRepository`. Depends on nothing else.
- `service`: use cases (catalogue, cart, checkout, certificates). Depends only on `domain`.
  Never on `infra` or `ui`.
- `infra`: in-memory repositories, payment SDK wrappers, console notifiers.
  Implements the `domain` interfaces.
- `ui`: the console menu. Calls `service` only, never `infra`.
- `App`: the composition root. The only place that creates concrete classes with `new`
  and passes them through constructors. No Singleton is used as a hidden global.

These rules are checked by `ArchitectureTest` (ArchUnit) and must stay green in every pull request.

## Consequences
Good: business rules are tested without the console, a database or the network; the
storage can be replaced in M4 without changing services; each layer has one job.
Bad / cost: more interfaces and packages than a one-package program, and new team
members must learn where each class belongs.
