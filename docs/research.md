# Research notes – Online Course Marketplace

Team: Forogh Anoosh (Maintainer), Shabir Mohibi (Architect)
Topic 3 of the capstone: Online Course Marketplace

This file lists what we looked at before and during M1–M2, and how it changed our design.

## 1. Real course platforms

| Platform | What we saw | What we took for our app |
|---|---|---|
| Udemy | A shopping cart for many courses; you can remove a course from the cart; discount codes are added in the cart, not on the checkout page; a certificate of completion after finishing a paid course | Cart with add/remove (US-04, US-05), cart total (US-06), discount code in the cart (US-07, M3), certificate only after completion (US-10) |
| Coursera | A course certificate after completing the course; the certificate has the course name and can be downloaded as PDF | Certificate shows the student name, course title and date; more than one format (text, HTML) |
| Stepik | Free and paid courses, many courses come with a "Stepik certificate" | Courses have a price; an instructor can add a course (US-01) |

Sources:
- Udemy – Using the Shopping Cart: https://support.udemy.com/hc/articles/229233447-Using-the-Shopping-Cart
- Udemy – Shopping Cart FAQ: https://support.udemy.com/hc/articles/229233487-Shopping-Cart-FAQ
- Udemy – Guest checkout FAQ (discount codes in the cart): https://support.udemy.com/hc/en-us/articles/29612777882391-Guest-checkout-Frequently-asked-questions
- Udemy – Download your certificate of completion: https://support.udemy.com/hc/articles/229603868
- Coursera – What is Coursera (certificates): https://coursera.org/articles/what-is-coursera
- Stepik – course page example: https://stepik.org/course/1111/promo

## 2. Payments in Kazakhstan

| Fact | Why it matters for us |
|---|---|
| The tenge (KZT, ISO 4217 numeric code 398) has 100 tiyn; its minor unit is 2 | KaspiAdapter converts tenge × 100 = tiyn |
| Payment gateways for Kaspi send the amount in minor units (no decimal point) and the currency "KZT" | Different SDKs want different formats → Adapter pattern |
| Kaspi Pay works with redirect / QR: the buyer pays in the Kaspi.kz app and returns to the shop | Our KaspiSdk is a fake class that only returns "OK" or "ERROR"; no real network in tests |
| Bank cards (for example Halyk ePay) need an amount, a currency and an invoice id | CardSdk.authorize(currency, amount, description) |

Sources:
- ISO 4217 / KZT: https://en.wikipedia.org/wiki/Kazakhstani_tenge
- JetPay – Kaspi Bank payment method (amount in minor units, currency KZT): https://developers.jetpay.kz/en/pm_kaspi.html
- Antom – Kaspi payment flow: https://docs.antom.com/ac/antomop/kaspi.md
- Halyk ePay payment widget: https://epayment.kz/en-US/docs/platezhnyi-vidzhet

## 3. Design patterns

Main sources:
- Our lectures 3–6 (Factory Method, Adapter, Observer, Template Method)
- E. Gamma, R. Helm, R. Johnson, J. Vlissides, "Design Patterns: Elements of Reusable Object-Oriented Software" (the "Gang of Four" book), 1994
- Refactoring.Guru – Design Patterns: https://refactoring.guru/design-patterns
- Refactoring.Guru Java examples: https://github.com/RefactoringGuru/design-patterns-java

| Pattern | Definition (GoF, simple words) | Where it is used in real Java |
|---|---|---|
| Factory Method | The parent class has a method that creates an object; subclasses decide which class to create | java.util.Calendar.getInstance(), Collection.iterator() |
| Adapter | Wraps a class with a different interface so it fits the interface the client expects | java.io.InputStreamReader (byte stream → character reader), Arrays.asList() |
| Observer | A subject keeps a list of listeners and tells all of them when something happens | Swing/AWT event listeners, java.util.concurrent.Flow (Publisher/Subscriber) |
| Template Method | A method fixes the steps of an algorithm; subclasses fill some steps | java.util.AbstractList, javax.servlet.http.HttpServlet.service() |

## 4. What the research changed in our design

1. The cart works like Udemy: add, remove, total, and later a discount code (Strategy in M3).
2. Payment providers are very different (tiyn vs tenge, "OK" vs 200), so we put an Adapter between our code and each SDK.
3. A new payment method must not change the checkout, so processors use Factory Method.
4. After payment several things happen (receipt, enrollment, later email), so we use Observer.
5. A certificate is only given after completion and can have different formats, so we use Template Method with a final generate().
