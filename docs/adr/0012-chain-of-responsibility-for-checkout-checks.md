# ADR-0012: Chain of Responsibility for the checks before checkout

- Status: accepted
- Date: 2026-11-25
- Deciders: Shabir Mohibi (Architect), Forogh Anoosh (Maintainer)

## Context
Before a student pays, several rules must hold:
1. the cart is not empty,
2. the student does not already have one of the courses in "My courses" (otherwise they pay twice),
3. the payment method is one that the shop offers,
4. one order is not above a total limit (protects against a wrong price).

Until now `CheckoutService.checkout()` only checked rule 1 with an `if`. Rule 2 was missing: a student could buy the
same course again. Adding every rule as another `if` would make `checkout()` long, and each new rule would edit the
service that also handles payment and the Observers. Milestone M4 asks for "Chain or Mediator".

## Options considered
1. **More `if`s in `CheckoutService.checkout()`.** Good: no new classes. Bad: the method grows with every rule, the rules
   can only be tested through the whole checkout, and their order is hidden in the code.
2. **Chain of Responsibility.** An abstract `CheckoutCheck` with `setNext(next)` and `check(...)`; one small class per rule.
   Each check either stops the order with an `IllegalStateException` and a clear message, or passes it to the next one.
   Good: one rule per class, each tested alone; a new rule is a new class plus one line in `App`; the order is visible
   where the chain is built. Bad: four small classes and one abstract class.
3. **Mediator.** A central object that coordinates cart, student, payment and checkout. Bad: our objects do not talk to
   each other in many directions; we only need a sequence of yes/no rules. A Mediator would add a coordinator
   with nothing to coordinate (YAGNI).

## Decision
We chose option 2 (Chain of Responsibility).
- `service`: `CheckoutCheck` (abstract, `setNext` returns the next check so the chain reads `a.setNext(b).setNext(c)`;
  `check` is `final`, so a check cannot forget to call the next one), `CartNotEmptyCheck`, `NotAlreadyBoughtCheck`,
  `PaymentMethodCheck`, `MaxTotalCheck`.
- `App` (composition root) builds the chain in this order: cart not empty → not already bought → payment method →
  maximum total (500 000 KZT). Cheap and most common problems come first.
- `CheckoutService` runs the chain **before** it creates the order and calls the payment provider. It does not know
  which checks exist. Its old constructors use a chain with only `CartNotEmptyCheck`, so older code and tests keep
  their behaviour.
- A failed check throws `IllegalStateException`. The REST API already returns that as HTTP 409, the website shows the
  message, and the console menu prints `Error: ...`.

## Consequences
Good: the "buy the same course twice" bug is fixed; every rule has its own test (`CheckoutCheckTest`) without a
database; `CheckoutServiceTest.chainStopsTheOrderBeforePayment` shows that a stopped order is not paid, no listener
is told, the cart is kept and no order number is used.
Bad / cost: five more classes. The chain stops at the **first** failed rule, so a student with two problems sees one
message at a time. If we ever need all messages at once, the checks would collect errors instead of throwing.
