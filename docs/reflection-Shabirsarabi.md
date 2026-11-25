# Reflection — Shabir Mohibi (@Shabirsarabi, Architect)

## 1. Which pattern did you implement, and what would the code look like without it?
I implemented the Strategy pattern for discount codes (US-07). `DiscountStrategy` is the interface, `PercentDiscount` and
`FixedAmountDiscount` are the rules, and `Cart` keeps one strategy and uses it in `total()`.
Without Strategy, `Cart.total()` would need an `if`/`switch` for every type of discount. The biggest problem would be
adding new things: every new discount rule would mean changing `Cart`, an old class that already works and is tested.
With Strategy, a new discount is only a new class and one line in `App`, and `Cart` does not change.

## 2. Where did you over-engineer first, and how did you notice?
I think the payment part is bigger than it needs to be for this project. For two fake SDKs we have `PaymentGateway`,
two adapters, two SDK classes, `PaymentProcessor` and two processor subclasses: eight types for "take the money".
Factory Method and Adapter together make sense when there are many real providers, but here both SDKs never fail
and never use the network.
 I noticed it when I drew the architecture diagram for the README: the payment boxes take more space than the rest
of the infra layer.

## 3. Which pattern would you remove if you started over? Why?
I would remove the Facade (`LearningService`), even though I added it myself. It is a small class, and for a console
app the menu could call `Student` and the certificate generators directly.
The cost of removing it is that the rule "you can only finish a course you bought" would go back into the UI, and it
would be harder to test without the console. ✏ For a bigger app with a web page I would keep it, but for this project
it adds one more layer for very little.
