# ADR-0005: Facade for "My courses" and certificates

- Status: accepted
- Date: 2026-11-25
- Deciders: Shabir Mohibi (Architect), Forogh Anoosh (Maintainer)

## Context
The pattern audit (ADR-0002, smell 2) found a business rule in the UI: `ConsoleMenu.completeAndCertify()` checked
that the course is in "My courses", marked it completed, chose a `CertificateGenerator` and built the certificate.
Problems:
- The rule "you can only finish a course you bought" could only be tested through the console.
- A second UI (for example a web page in M4/M5) would have to copy these steps.
- Bug: the course was marked completed **before** the format was checked, so a wrong format left a completed course without a certificate.

## Options considered
1. Keep the steps in `ConsoleMenu`. Good: no new class. Bad: the problems above stay.
2. **Facade**: one service, `LearningService`, with simple methods (`myCourses`, `findMyCourse`, `certificateFormats`,
   `completeAndCertify`). It hides `Student` and the `CertificateGenerator` family from the UI.
   Good: the UI only asks and prints; the rule and the order of steps are in one tested place.
   Bad: one more class, and the menu calls `findMyCourse` once early to give a quick error message.
3. One big "application service" for the whole app (catalogue, cart, checkout, learning). Bad: a god class; the other
   services are already small and clear. YAGNI.

## Decision
We chose option 2. `LearningService` is in the service layer and gets the certificate generators from `App`
(composition root). `completeAndCertify()` checks the course **and** the format first, and only then marks the course
completed and builds the certificate. `ConsoleMenu` no longer knows `CertificateGenerator`.

## Consequences
Good: the rule is tested without the console (`LearningServiceTest`), the wrong-format bug is fixed, and a new UI
reuses the same method. The Template Method (row 4) is unchanged: the Facade only uses it.
Bad / cost: one more class. If certificates later need saving (M4), `LearningService` is the one place to add it.
Audit smell 3 (State for `Order`) is still open.
