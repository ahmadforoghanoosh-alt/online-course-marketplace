# ADR-0003: Strategy for discount codes

- Status: accepted
- Date: 2026-11-25
- Deciders: Shabir Mohibi (Architect), Forogh Anoosh (Maintainer)

## Context
US-07: a student enters a discount code, a valid code lowers the total "by its rule", an invalid code shows a
message and the total does not change. We already need two different rules (10% off, 2000 KZT off) and
ADR-0002 (smell 4) says `Cart.total()` has no place for them yet. More rules will come (for example "buy 2, get 1 cheaper").

## Options considered
1. `if`/`switch` on the code inside `Cart.total()`. Good: very little code today. Bad: every new rule edits `Cart`
   (breaks Open/Closed), `Cart` must know all codes, and the rules can not be tested alone.
2. **Strategy**: a `DiscountStrategy` interface with `apply(total)`, one class per rule. `Cart` keeps one strategy and
   calls it in `total()`. A `DiscountService` in the service layer maps a code to a strategy.
   Good: a new rule is a new class; each rule has its own small test; `Cart` does not know codes.
   Bad: two more classes and one interface.
3. **Decorator** around `Cart` or around a strategy, so several discounts can be stacked.
   Good: combines rules. Bad: we have no story for stacking codes; it adds wrapping that nobody needs now (YAGNI).

## Decision
We chose option 2 (Strategy).
- `domain`: `DiscountStrategy`, `PercentDiscount`, `FixedAmountDiscount`; `Cart` has `applyDiscount()`, `subtotal()` and `total()`.
- `service`: `DiscountService.applyCode(cart, code)`. Codes are not case sensitive. Unknown code → `IllegalArgumentException`,
  the cart is not changed. Empty cart → `IllegalStateException`.
- `App` (composition root) decides which codes exist: `STUDENT10` (10%) and `WELCOME2000` (2000 KZT).
- `Order` gets the total from the cart, so the student pays the discounted total. A free order (total 0) is marked paid
  without calling the payment provider, because the SDKs reject 0.
- A new code replaces the old one (one discount per cart). `Cart.clear()` also removes the discount.

## Consequences
Good: a new discount rule is one new class plus one line in `App`; `Cart` and `CheckoutService` do not change.
The acceptance criteria of US-07 are covered by `DiscountServiceTest` and `ConsoleMenuTest.discountCodeChangesCartTotal`.
Bad / cost: three new types. If we ever need stacked discounts we add a Decorator then (option 3) and write a new ADR.
The codes live in memory in `App`; with persistence (M4) they move to a repository.
