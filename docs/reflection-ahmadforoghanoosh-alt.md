# Reflection — Forogh Anoosh (@ahmadforoghanoosh-alt, Maintainer)

## 1. Which pattern did you implement, and what would the code look like without it?
I want to write about the Strategy pattern for the payment method. Our teacher told us to use it, and I added it.
`PaymentStrategy` is an interface with `name()` and `pay(order)`. `CheckoutService` only knows this interface.
The student chooses Kaspi or bank card in the menu, and checkout uses the chosen strategy.

Without Strategy, `CheckoutService.checkout()` would need something like
`if (method == "Kaspi") ... else if (method == "Card") ...`. Every new payment method would change checkout,
and checkout is an important class with many tests. With Strategy, a new payment method is one new class.
In `PaymentStrategyTest` I added a "Gift card" payment, and `CheckoutService` did not change at all.

At first we did not see Strategy here. In M2 we used Factory Method and Adapter, because our problem was creating
the right gateway and the two SDKs were different. Our code already worked a little like a Strategy, but it was
not clear. Now the three patterns work together: Strategy chooses how to pay, Factory Method creates the gateway,
and Adapter talks to the SDK.

## 2. Where did you over-engineer first, and how did you notice?
I think the payment part is too big for this project. For two fake SDKs we have `PaymentStrategy`,
`PaymentProcessor`, two processor classes, `PaymentGateway`, two adapters and two SDK classes.
That is many classes, but our SDKs never use the network and almost never fail.

I noticed it when I added the SQLite database (ADR-0004). For the database I only wrote one new class,
`SqliteCourseRepository`, and changed `App`. Services and tests did not change. It was small and easy.
Then I compared it with the payment part and saw how many classes we have there for a simple job.
For real banks this structure is useful, but for our console app it is more than we need.

## 3. Which pattern would you remove if you started over? Why?
I would remove the Facade (`LearningService`). It is a small class, and in a console app the menu could call
`Student` and the certificate generators directly. It adds one more layer for a small job.

But removing it has a cost. The rule "you can only finish a course you bought" would go back into
`ConsoleMenu`, and then we could test it only through the console. If we later make a web page,
we would need to copy this rule. So for a bigger app I would keep it, but for this project I think it is not really needed.
