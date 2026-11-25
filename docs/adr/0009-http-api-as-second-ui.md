# ADR-0009: A REST API as a second user interface

- Status: accepted
- Date: 2026-11-25
- Deciders: Forogh Anoosh (Maintainer), Shabir Mohibi (Architect)

## Context
Our teacher asked about the final application and an API. The capstone says: "a console menu or a simple web or
desktop UI. Interface quality is not graded; the separation from the service layer is."
The best way to show this separation is a second UI that uses the same services without changing them.

## Options considered
1. Console only. Good: nothing to do. Bad: we can only say the layers are separated, we can not show it.
2. Spring Boot REST API. Good: popular. Bad: a big framework; the rules allow frameworks only if the patterns stay
   visible, and Spring would create and wire our objects itself (dependency injection), hiding our composition root.
3. The HTTP server that comes with the JDK (`com.sun.net.httpserver`). Good: no new library, `App` still wires
   everything, small code. Bad: we write the routing and a tiny JSON writer ourselves.

## Decision
We chose option 3. In the `ui` package:
- `ApiController` maps a method + path + query to a service call and returns an `ApiResponse` (status + JSON).
  It does not know HTTP, so `ApiControllerTest` tests it without a network (the capstone forbids network in tests).
- `HttpApiServer` only connects the controller to the JDK HTTP server.
- `Json` writes maps, lists, strings and numbers as JSON.
- `App --api` starts the API on port 8080 instead of the console menu. `App` is still the only composition root.
Errors: bad input = 400, not allowed now (empty cart, nothing to undo) = 409, unknown endpoint or course = 404.

## Consequences
Good: the console menu and the API use exactly the same `CatalogService`, `CartService`, `CartHistory` and commands,
`CheckoutService` with `PaymentStrategy`, `DiscountService` and `LearningService`; no service changed.
`ArchitectureTest` still passes: `ui` does not use `infra`.
Bad / cost: one student only (the API is for the demo, no login); `HttpApiServer` itself is not unit-tested,
because starting a server would need the network; it has no logic of its own.
