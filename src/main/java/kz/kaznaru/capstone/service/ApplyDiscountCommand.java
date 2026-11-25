package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.DiscountStrategy;

/** Command: apply a discount code. Undo brings back the old discount, or no discount. */
public class ApplyDiscountCommand implements CartCommand {
    private final DiscountService discounts;
    private final Cart cart;
    private final String code;
    private DiscountStrategy previous;
    private DiscountStrategy applied;

    public ApplyDiscountCommand(DiscountService discounts, Cart cart, String code) {
        this.discounts = discounts;
        this.cart = cart;
        this.code = code;
    }

    @Override
    public void execute() {
        previous = cart.getDiscount();
        applied = discounts.applyCode(cart, code);
    }

    @Override
    public void undo() {
        if (previous == null) {
            cart.removeDiscount();
        } else {
            cart.applyDiscount(previous);
        }
    }

    @Override
    public String describe() {
        return "discount " + (applied == null ? code : applied.describe());
    }

    public DiscountStrategy getApplied() {
        return applied;
    }
}
