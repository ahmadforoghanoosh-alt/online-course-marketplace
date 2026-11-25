package kz.kaznaru.capstone.domain;

/** Strategy: one rule that changes the cart total (US-07). */
public interface DiscountStrategy {
    /** Returns the new total. Never less than 0. */
    double apply(double total);

    /** Short text for the cart, for example "10% off". */
    String describe();
}
