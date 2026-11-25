# Pattern Register

Fill one row per pattern **actually used**. Each row must point to real classes and a test.
Delete the TODO rows as you replace them. `scripts/check-deliverables.sh` counts rows.

| # | Pattern | Lecture | Problem in this project (the smell it removes) | Key classes | Test | Alternative considered |
|---|---------|---------|-----------------------------------------------|-------------|------|------------------------|
| 1 | Factory Method (Creational) | L3 | Checkout needed "if Kaspi new KaspiAdapter, else if card new CardAdapter", one more branch for every payment type | `PaymentProcessor` (abstract `createGateway()`), `KaspiPaymentProcessor`, `CardPaymentProcessor` | `PaymentProcessorTest`, `PaymentProcessorsTest` | `switch` on a payment name in `CheckoutService`: rejected, it grows and breaks Open/Closed |
| 2 | Adapter (Structural) | L4 | Kaspi SDK works in tiyn and returns "OK"/"ERROR"; the card SDK needs a currency and returns a status number. Our code wants one `charge()` | `PaymentGateway`, `KaspiAdapter` + `KaspiSdk`, `CardAdapter` + `CardSdk` | `KaspiAdapterTest`, `CardAdapterTest` | Calling the SDKs directly in the service: ties the service to the vendors |
| 3 | Observer (Behavioral) | L5 | After payment, checkout had to call the receipt printer and the enrollment code directly, and grow with every new reaction | `OrderListener`, `CheckoutService` (subject), `EnrollmentListener`, `ConsoleReceiptNotifier` | `CheckoutServiceTest`, `ConsoleReceiptNotifierTest` | Direct calls inside `checkout()`: rejected, every new reaction edits `CheckoutService` |
| 4 | Template Method (Behavioral) | L6 | Text and HTML certificates repeat the same steps (check completed, header, body, footer); only the format differs | `CertificateGenerator` (final `generate()`), `TextCertificateGenerator`, `HtmlCertificateGenerator` | `CertificateGeneratorTest` | Two separate classes with copy-pasted steps: a fix must be made twice |
| 5 | Strategy (Behavioral) | L7 | US-07 needs different discount rules (percent off, fixed amount off). Without a pattern `Cart.total()` would get an `if`/`switch` on the code type that grows with every new rule | `DiscountStrategy`, `PercentDiscount`, `FixedAmountDiscount`, `Cart` (context), `DiscountService` (code to strategy) | `DiscountStrategyTest`, `DiscountServiceTest`, `CartTest` | `switch` on the code in `Cart.total()`: rejected, every new rule edits `Cart`. Decorator to stack discounts: rejected for now (YAGNI), one code per cart, see [ADR-0003](adr/0003-strategy-for-discounts.md) |

## Audit log
| Date | What we checked | Result | ADR |
|------|-----------------|--------|-----|
| 2026-11-25 (week 7) | All 4 patterns and the services after v0.2 | All 4 patterns kept. Removed the pass-through methods `removeFromCart()` and `total()` from `CartService` (middle man smell). Singleton rejected | [ADR-0002](adr/0002-pattern-audit-week-7.md) |

## Rejected patterns
At least two patterns you considered and decided NOT to use, with the reason (YAGNI). Link the ADR.

| Pattern | Why rejected | ADR |
|---------|--------------|-----|
| Singleton (Creational) | `App` already creates one object of each service and passes it in. A Singleton is a global variable: it hides the dependency and tests would share state | [ADR-0002](adr/0002-pattern-audit-week-7.md) |
