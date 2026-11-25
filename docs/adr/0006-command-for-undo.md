# ADR-0006: Command pattern for undo in the cart (US-05)

- Status: accepted
- Date: 2026-11-25
- Deciders: Forogh Anoosh (Maintainer), Shabir Mohibi (Architect)

## Context
US-05 asks: "After undo, the cart is exactly the same as before the last change." The capstone also asks for
an undo or history feature (Command or Memento) with a test that proves the state returns exactly.
The cart can change in three ways: add a course (US-04), remove a course (US-05) and apply a discount code (US-07).
Before this change `ConsoleMenu` called `cartService.addToCart()`, `cart.remove()` and `discounts.applyCode()`
directly, so nothing remembered what happened and nothing could be reversed.

## Options considered
1. Command: every cart change is an object with `execute()` and `undo()`; `CartHistory` keeps the done commands
   on a stack. Good: each command saves only what it needs (the removed course and its place, the old discount);
   a new kind of change is a new class; the history can also show what was undone. Bad: one class per kind of change.
2. Memento: before every change, save a full copy of the cart (courses + discount) and put it back on undo.
   Good: one generic mechanism. Bad: `Cart` must create and accept snapshots of its private state, we copy the whole
   cart for every small change, and the menu would have to remember to take a snapshot before each action.
3. No pattern: the menu remembers "last action" in a few variables and reverses it with `if`/`switch`.
   Bad: only one step back, and every new cart action edits the menu again.

## Decision
We chose option 1, Command (Lecture 5).
- `CartCommand` (interface: `execute()`, `undo()`, `describe()`)
- `AddToCartCommand`, `RemoveFromCartCommand` (remembers the index, so the order is the same after undo),
  `ApplyDiscountCommand` (remembers the previous discount, or none)
- `CartHistory` is the invoker: `run()` saves a command only if `execute()` worked; `undo()` pops the last one;
  `clear()` is called after a successful payment, because the paid courses must not come back into the cart.
- `Cart` got two small methods for undo: `restore(index, course)` and `removeDiscount()`.
- `ConsoleMenu` creates the commands and has a new option "U. Undo last cart change".

We reject Memento for now: Command already gives exact undo with less copying, and the cart state is small.
If later we need "versions" of a whole object (for example saved carts), Memento can be added then.

## Consequences
Good: US-05 is done; several undos go back step by step; `CartHistoryTest` proves that the courses, their order,
the discount and the total are exactly the same after undo; a failed action (unknown course, unknown code) is not
saved, so undo never reverses something that did not happen.
Bad / cost: four small classes in `service`; every future kind of cart change must also be written as a command.
