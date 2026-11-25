# ADR-0007: Strategy for the payment method, combined with Factory Method

- Status: accepted
- Date: 2026-11-25
- Deciders: Forogh Anoosh (Maintainer), Shabir Mohibi (Architect)

## Context
At the M3 consultation our teacher said the payment method should use the Strategy pattern.
US-08: "I can choose the payment method at checkout." The student picks Kaspi or card at run time, and
`CheckoutService` must work the same way with either one. Until now `CheckoutService.checkout()` asked for the
abstract class `PaymentProcessor`, so the only way to add a payment method was to extend that class and use
our gateway/adapter structure, even for a method that does not need a gateway (for example a gift card).

## Options considered
1. Keep `PaymentProcessor` only. Good: no change. Bad: checkout depends on an abstract class with a factory
   method inside; the "choose one algorithm at run time" idea is hidden, and every new method must create a gateway.
2. Add a `PaymentStrategy` interface (`name()`, `pay(order)`). `CheckoutService` (the context) and `ConsoleMenu`
   only use this interface. `PaymentProcessor` implements it and keeps its Factory Method for Kaspi and card.
   Good: a clear Strategy; a new payment method is one new class; Factory Method and Adapter stay where they help.
   Bad: one more small interface.
3. Replace Factory Method with Strategy completely (Kaspi and card implement the interface directly and create
   their adapters themselves). Bad: the creation of the gateway would be copied into every strategy, and we lose
   our only creational pattern in the code.

## Decision
We chose option 2. This is the combination of two patterns working together (Lecture 7):
- Strategy: `PaymentStrategy` = the algorithm "how to pay"; `CheckoutService` = context; the student chooses the
  strategy in the menu at run time.
- Factory Method: `PaymentProcessor implements PaymentStrategy`; `KaspiPaymentProcessor` and `CardPaymentProcessor`
  decide in `createGateway()` which adapter to create.
- Adapter: `KaspiAdapter` and `CardAdapter` translate our `charge()` to each vendor SDK.

## Consequences
Good: `CheckoutService`, `ConsoleMenu` and `App` depend on the interface; `PaymentStrategyTest` adds a gift-card
payment as one new class without changing checkout (Open/Closed extension point); all old tests still pass.
Bad / cost: one more interface; developers must know that a Kaspi or card payment is both a Strategy and a
Factory Method.
