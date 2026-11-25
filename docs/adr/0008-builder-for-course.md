# ADR-0008: Builder for creating a Course

- Status: accepted
- Date: 2026-11-25
- Deciders: Forogh Anoosh (Maintainer), Shabir Mohibi (Architect)

## Context
The capstone asks for at least 2 Creational patterns; we had only Factory Method.
We also had a real smell when creating courses: `new Course(id, title, instructor, description, price)` has
three `String` parameters in a row. If someone swaps `title` and `instructor`, the code still compiles and the
mistake is found only on the screen. The same long call was written in `InMemoryCourseRepository`,
`SqliteCourseRepository` (three times, one of them only to check the values before saving) and
`FakeCourseRepository`. Description and instructor are not always known, but there was no safe default.

## Options considered
1. Keep the constructor. Good: no new code. Bad: the swap mistake stays possible; no defaults.
2. Builder (Lecture 9): `Course.builder().title(...).instructor(...).price(...).build()`. Good: every value has a
   name; optional parts get defaults ("Unknown instructor", empty description); `build()` checks the required parts.
   Bad: a small extra class inside `Course`.
3. A static factory method with the same five parameters. Bad: the same parameter order problem.

## Decision
We chose option 2. `Course.Builder` is a nested class in the domain (no dependencies). The record constructor stays,
so the rules for title and price live in one place and the builder only adds names and defaults.
The repositories (in-memory, SQLite and the fake one in tests) now create courses with the builder.

## Consequences
Good: two Creational patterns (Factory Method, Builder); the "same check before saving" in `SqliteCourseRepository`
is now a clear `draft` built by the builder; `CourseBuilderTest` covers names, defaults and checks.
Bad / cost: longer code at each place that creates a course. We did not change `CourseRepository.add(...)`,
so services and the UI did not change.
