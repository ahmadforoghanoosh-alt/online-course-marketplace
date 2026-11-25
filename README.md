# Online Course Marketplace

> Capstone project — Application Design Based on Patterns (Design Patterns), 4th year, Information Systems.

**Team:** Forogh Anoosh (@ahmadforoghanoosh-alt), Shabir Mohibi (@Shabirsarabi)

**Roles (M1):** Forogh Anoosh: Maintainer · Shabir Mohibi: Architect

**Domain:** Instructors publish online courses, and students browse them, put them in a cart, pay with Kaspi or a bank card, get enrolled and receive a certificate when they finish.

Requirements: [docs/user-stories.md](docs/user-stories.md)

## Quick start
```bash
mvn verify                      # build + tests + coverage gate
mvn exec:java -Dexec.mainClass=kz.kaznaru.capstone.App   # or press F5 in VS Code
mvn exec:java -Dexec.mainClass=kz.kaznaru.capstone.App -Dexec.args="--memory"   # without the database
```

## REST API (second UI)
Start the same application as an HTTP API instead of the console menu ([ADR-0009](docs/adr/0009-http-api-as-second-ui.md)):
```bash
mvn exec:java -Dexec.mainClass=kz.kaznaru.capstone.App -Dexec.args="--api"   # http://localhost:8080
```
In VS Code: open Run and Debug, choose **Run API (port 8080)** and press F5.

| Method | Path | What it does |
|---|---|---|
| GET | `/courses` | all courses |
| GET | `/courses/{id}` | one course (404 if not found) |
| POST | `/courses?title=..&instructor=..&description=..&price=..` | add a course (201) |
| GET | `/cart` | cart with subtotal, discount and total |
| POST | `/cart/{id}` | add a course to the cart |
| DELETE | `/cart/{id}` | remove a course from the cart |
| POST | `/cart/discount?code=STUDENT10` | apply a discount code |
| POST | `/cart/undo` | undo the last cart change (Command) |
| GET | `/payment-methods` | Kaspi, Bank card (Strategy) |
| POST | `/checkout?method=Kaspi` | pay; the receipt is printed in the server console (Observer) |
| GET | `/my-courses` | the student's courses |
| POST | `/my-courses/{id}/certificate?format=Text` | complete a course and get a certificate (Facade + Template Method) |

Example: `curl -X POST "http://localhost:8080/cart/1"`

## Database
Courses are saved in a SQLite database file, `marketplace.db`, created in the project folder on the first run
(SQLite through JDBC, `SqliteCourseRepository`). Start with `--memory` to use the in-memory repository instead.
The services do not change: both classes implement `CourseRepository` ([ADR-0004](docs/adr/0004-sqlite-course-repository.md)).

| Table `courses` | Type |
|---|---|
| id | INTEGER PRIMARY KEY AUTOINCREMENT |
| title | TEXT NOT NULL |
| instructor | TEXT NOT NULL |
| description | TEXT NOT NULL |
| price | REAL NOT NULL, CHECK (price > 0) |

## Architecture
Four layers ([ADR-0001](docs/adr/0001-layers-and-package-rules.md)). Arrows show "uses". The rules are checked by
`ArchitectureTest`: `domain` uses nothing else, `service` does not use `infra` or `ui`, `ui` does not use `infra`.
Only `App` (the composition root) creates the concrete `infra` classes and wires everything together.

```mermaid
flowchart TB
  App["App<br/>composition root"]

  subgraph UI["ui"]
    Menu["ConsoleMenu"]
    Api["ApiController + HttpApiServer<br/>REST API, second UI"]
  end

  subgraph SERVICE["service"]
    Catalog["CatalogService"]
    CartSvc["CartService"]
    History["CartHistory + cart commands<br/>Command"]
    Discounts["DiscountService"]
    Checkout["CheckoutService<br/>Observer: subject"]
    Learning["LearningService<br/>Facade"]
    PayStrat["PaymentStrategy<br/>Strategy"]
    Processor["PaymentProcessor<br/>Factory Method"]
    CertGen["CertificateGenerator<br/>Template Method"]
    Enroll["EnrollmentListener<br/>Observer"]
  end

  subgraph DOMAIN["domain"]
    Cart["Cart<br/>Strategy: context"]
    Strategy["DiscountStrategy<br/>PercentDiscount, FixedAmountDiscount"]
    Order["Order, Student, Course<br/>Builder: Course.builder()"]
    RepoPort["CourseRepository<br/>Repository port"]
    Gateway["PaymentGateway<br/>Adapter target"]
    Listener["OrderListener"]
  end

  subgraph INFRA["infra"]
    Repos["InMemoryCourseRepository<br/>SqliteCourseRepository"]
    Adapters["KaspiAdapter, CardAdapter<br/>Adapter"]
    Sdks["KaspiSdk, CardSdk<br/>vendor code"]
    Procs["KaspiPaymentProcessor<br/>CardPaymentProcessor"]
    Receipt["ConsoleReceiptNotifier<br/>Observer"]
  end

  App --> Menu
  App --> Api
  Api --> Catalog & CartSvc & Discounts & Checkout & Learning & History
  App -.creates.-> Repos & Procs & Receipt

  Menu --> Catalog & CartSvc & Discounts & Checkout & Learning & History
  History --> CartSvc & Discounts & Cart
  Catalog & CartSvc --> RepoPort
  Discounts --> Cart
  Cart --> Strategy
  Checkout --> PayStrat
  Processor -.implements.-> PayStrat
  Checkout --> Listener
  Checkout --> Order
  Learning --> CertGen
  Learning --> Order
  Processor --> Gateway
  Enroll -.implements.-> Listener

  Repos -.implements.-> RepoPort
  Procs -.extends.-> Processor
  Procs --> Adapters
  Adapters -.implements.-> Gateway
  Adapters --> Sdks
  Receipt -.implements.-> Listener
```

| Pattern | Category | Where | Register row |
|---------|----------|-------|--------------|
| Factory Method | Creational | `PaymentProcessor` → `KaspiPaymentProcessor`, `CardPaymentProcessor` | 1 |
| Adapter | Structural | `KaspiAdapter`, `CardAdapter` → `PaymentGateway` | 2 |
| Observer | Behavioral | `CheckoutService` → `EnrollmentListener`, `ConsoleReceiptNotifier` | 3 |
| Template Method | Behavioral | `CertificateGenerator` → `TextCertificateGenerator`, `HtmlCertificateGenerator` | 4 |
| Strategy | Behavioral | `Cart` + `DiscountStrategy` → `PercentDiscount`, `FixedAmountDiscount`; `CheckoutService` + `PaymentStrategy` → Kaspi / card processors | 5 |
| Repository | Architectural | `CourseRepository` → `InMemoryCourseRepository`, `SqliteCourseRepository` | 6 |
| Facade | Structural | `LearningService` over `Student` and the certificate generators | 7 |
| Strategy + Factory Method + Adapter | Combination | the chosen `PaymentStrategy` creates its gateway (`createGateway()`), which adapts the vendor SDK | 5 + 1 + 2 |
| Builder | Creational | `Course.builder()...build()` in the repositories | 9 |
| Command | Behavioral | `CartHistory` runs and undoes `AddToCartCommand`, `RemoveFromCartCommand`, `ApplyDiscountCommand` | 8 |

## Patterns used
See [docs/PATTERN_REGISTER.md](docs/PATTERN_REGISTER.md). Decisions: [docs/adr](docs/adr).

## Team workflow
Branch per issue, pull request per branch, one teammate review before merge.
