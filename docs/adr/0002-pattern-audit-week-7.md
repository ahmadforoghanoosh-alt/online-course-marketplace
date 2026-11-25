# ADR-0002: Pattern audit after M2 (week 7)

- Status: accepted
- Date: 2026-11-25
- Deciders: Shabir Mohibi (Architect), Forogh Anoosh (Maintainer)

## Context
After v0.2 we read our own code again and looked for smells and for patterns or classes that we do not need.
We found these smells:

| # | Smell | Where | What we do |
|---|-------|-------|------------|
| 1 | Middle man: methods that only pass the call to another object | `CartService.removeFromCart()` only calls `cart.remove()`, `CartService.total()` only calls `cart.total()` | Remove now (this ADR) |
| 2 | Business rule in the UI | `ConsoleMenu.completeAndCertify()` checks that the course is in "My courses" | Move behind a Facade in M3 |
| 3 | Status changes are spread out and will grow (NEW, PAID, later CANCELLED) | `Order.markPaid()`, `CheckoutService.checkout()` | State pattern in M3 |
| 4 | No place for discount rules yet (US-07) | `Cart.total()` | Strategy pattern in M3 |
| 5 | The same "cart is empty" check in two places | `ConsoleMenu.doCheckout()` and `CheckoutService.checkout()` | Keep: the UI check only gives a nice message, the service check protects the rule |

We also checked the 4 patterns in the register. Each one still removes a real smell, so we keep all of them:
Factory Method (a new payment type is a new class), Adapter (two SDKs with different APIs),
Observer (receipt and enrollment react to a paid order), Template Method (text and HTML certificates share the steps).

## Options considered
1. Keep `CartService` as it is. Good: nothing changes. Bad: two methods add a layer that does nothing, and every new cart method would be written twice.
2. Remove the two pass-through methods; the menu calls `Cart` directly for remove and total. `CartService` keeps only `addToCart()`, because it really needs the repository. Good: less code, one place for each rule. Bad: the menu talks to one more domain class (allowed by ADR-0001: ui may use domain).
3. Delete `CartService` completely. Bad: the menu would need the repository to find a course by id, so the UI would do a use case.

We also thought about a Singleton for `CheckoutService` and the repository, so that every class can reach the same object.

## Decision
We chose option 2. `CartService` now has only `addToCart()`. `ConsoleMenu` calls `cart.remove(id)` and `cart.total()`.
The tests for remove and total stay in `CartTest`; `CartServiceTest` now tests only what the service adds.

We reject Singleton. `App` already creates exactly one object of each service and passes it in (composition root).
A Singleton would be a global variable: it hides the dependency and makes tests share state.

## Consequences
Good: less code, no pass-through methods, the register and the code still match. All tests stay green.
Bad / cost: if later remove needs a rule (for example "can not remove a paid course"), we add it to `Cart` or bring the method back to the service.
Smells 2, 3 and 4 are planned for M3 (Facade, State, Strategy).
