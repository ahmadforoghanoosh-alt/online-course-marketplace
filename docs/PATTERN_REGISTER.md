# Pattern Register

Fill one row per pattern **actually used**. Each row must point to real classes and a test.
Delete the TODO rows as you replace them. `scripts/check-deliverables.sh` counts rows.

| # | Pattern | Lecture | Problem in this project (the smell it removes) | Key classes | Test | Alternative considered |
|---|---------|---------|-----------------------------------------------|-------------|------|------------------------|
| 1 | Factory Method (Creational) | L3 | Checkout needed "if Kaspi new KaspiAdapter, else if card new CardAdapter", one more branch for every payment type | `PaymentProcessor` (abstract `createGateway()`), `KaspiPaymentProcessor`, `CardPaymentProcessor` | `PaymentProcessorTest`, `PaymentProcessorsTest` | `switch` on a payment name in `CheckoutService`: rejected, it grows and breaks Open/Closed |
| 2 | Adapter (Structural) | L4 | Kaspi SDK works in tiyn and returns "OK"/"ERROR"; the card SDK needs a currency and returns a status number. Our code wants one `charge()` | `PaymentGateway`, `KaspiAdapter` + `KaspiSdk`, `CardAdapter` + `CardSdk` | `KaspiAdapterTest`, `CardAdapterTest` | Calling the SDKs directly in the service: ties the service to the vendors |
| 3 | Observer (Behavioral) | L5 | After payment, checkout had to call the receipt printer and the enrollment code directly, and grow with every new reaction | `OrderListener`, `CheckoutService` (subject), `EnrollmentListener`, `ConsoleReceiptNotifier`, `OrderHistoryService` (saves paid orders, [ADR-0010](adr/0010-save-orders-in-the-database.md)) | `CheckoutServiceTest`, `ConsoleReceiptNotifierTest`, `OrderHistoryServiceTest` | Direct calls inside `checkout()`: rejected, every new reaction edits `CheckoutService` |
| 4 | Template Method (Behavioral) | L6 | Text and HTML certificates repeat the same steps (check completed, header, body, footer); only the format differs | `CertificateGenerator` (final `generate()`), `TextCertificateGenerator`, `HtmlCertificateGenerator` | `CertificateGeneratorTest` | Two separate classes with copy-pasted steps: a fix must be made twice |
| 5 | Strategy (Behavioral) | L7 | US-07 needs different discount rules (percent off, fixed amount off), and US-08 lets the student choose the payment method at checkout. Without a pattern `Cart.total()` would get an `if`/`switch` on the code type that grows with every new rule | `DiscountStrategy`, `PercentDiscount`, `FixedAmountDiscount`, `Cart` (context), `DiscountService` (code to strategy); `PaymentStrategy` with `CheckoutService` as context ([ADR-0007](adr/0007-strategy-for-payment-method.md)) | `DiscountStrategyTest`, `DiscountServiceTest`, `CartTest`, `PaymentStrategyTest` | `switch` on the code in `Cart.total()`: rejected, every new rule edits `Cart`. Decorator to stack discounts: rejected for now (YAGNI), one code per cart, see [ADR-0003](adr/0003-strategy-for-discounts.md) |
| 6 | Repository (Architectural) | L14 | Services needed the courses but must not know where they are stored; in-memory data was lost on restart | `CourseRepository`, `OrderRepository` (domain), `InMemoryCourseRepository`, `SqliteCourseRepository`, `InMemoryOrderRepository`, `SqliteOrderRepository` (infra), chosen in `App` | `InMemoryCourseRepositoryTest`, `InMemoryOrderRepositoryTest`, `CatalogServiceTest`, `CartServiceTest` (with `FakeCourseRepository`, `FakeOrderRepository`), `AppTest` | Services call JDBC directly: SQL in the service layer, cannot test without a database ([ADR-0004](adr/0004-sqlite-course-repository.md)) |
| 7 | Facade (Structural) | L8 | `ConsoleMenu.completeAndCertify()` held a business rule (only an enrolled course can be completed) and did the steps itself: find course, mark completed, pick generator, build certificate. The UI knew too much, and a wrong format still marked the course completed | `LearningService` (facade) over `Student` and the `CertificateGenerator` family; `ConsoleMenu` only asks and prints | `LearningServiceTest`, `ConsoleMenuTest` | Keep the steps in the menu: the rule can not be tested without the console, and a web UI would copy it. A full "application service for everything": too big, YAGNI ([ADR-0005](adr/0005-facade-for-certificates.md)) |
| 8 | Command (Behavioral) | L5 | US-05 needs "undo my last cart change". The menu changed the cart directly (`addToCart`, `remove`, `applyCode`), so nothing could be reversed | `CartCommand`, `AddToCartCommand`, `RemoveFromCartCommand`, `ApplyDiscountCommand`, `CartHistory` (invoker), used by `ConsoleMenu` | `CartHistoryTest`, `CartTest`, `ConsoleMenuTest` | Memento (a copy of the whole cart before every change): rejected, more copying and `Cart` must expose its state ([ADR-0006](adr/0006-command-for-undo.md)) |
| 9 | Builder (Creational) | L9 | `new Course(id, title, instructor, description, price)` has three Strings in a row, so title and instructor can be swapped without a compile error; no defaults for optional parts; the same long call in three repositories | `Course.Builder` (`Course.builder()`), used by `InMemoryCourseRepository`, `SqliteCourseRepository`, `FakeCourseRepository` | `CourseBuilderTest` | A static factory with the same five parameters: same order problem ([ADR-0008](adr/0008-builder-for-course.md)) |

## Combination of patterns
**Strategy + Factory Method + Adapter for payment** ([ADR-0007](adr/0007-strategy-for-payment-method.md)):
the student chooses a `PaymentStrategy` at checkout (Strategy, `CheckoutService` is the context);
`KaspiPaymentProcessor` and `CardPaymentProcessor` are strategies that create their own gateway (Factory Method);
the gateway is `KaspiAdapter` or `CardAdapter` over the vendor SDK (Adapter). Test: `PaymentStrategyTest`, `PaymentProcessorsTest`.

## Audit log
| Date | What we checked | Result | ADR |
|------|-----------------|--------|-----|
| 2026-11-25 (week 7) | All 4 patterns and the services after v0.2 | All 4 patterns kept. Removed the pass-through methods `removeFromCart()` and `total()` from `CartService` (middle man smell). Singleton rejected | [ADR-0002](adr/0002-pattern-audit-week-7.md) |
| 2026-11-25 | Audit smell 2 (business rule in the UI) | Fixed with a Facade: `LearningService`. Smells 3 (State for `Order`) still open | [ADR-0005](adr/0005-facade-for-certificates.md) |

## Rejected patterns
At least two patterns you considered and decided NOT to use, with the reason (YAGNI). Link the ADR.

| Pattern | Why rejected | ADR |
|---------|--------------|-----|
| Singleton (Creational) | `App` already creates one object of each service and passes it in. A Singleton is a global variable: it hides the dependency and tests would share state | [ADR-0002](adr/0002-pattern-audit-week-7.md) |
| Memento (Behavioral) | Undo with a full copy of the cart before every change. Command already gives exact undo and saves only what changed; the cart state is small | [ADR-0006](adr/0006-command-for-undo.md) |
