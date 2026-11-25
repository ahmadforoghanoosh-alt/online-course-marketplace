package kz.kaznaru.capstone.domain;

/** Takes a fixed amount of tenge off the total, but never below 0. */
public class FixedAmountDiscount implements DiscountStrategy {
    private final double amountKzt;

    public FixedAmountDiscount(double amountKzt) {
        if (!(amountKzt > 0) || Double.isInfinite(amountKzt)) {
            throw new IllegalArgumentException("amount must be greater than 0");
        }
        this.amountKzt = amountKzt;
    }

    @Override
    public double apply(double total) {
        return Math.max(0, total - amountKzt);
    }

    @Override
    public String describe() {
        return String.format(java.util.Locale.ROOT, "%.0f KZT off", amountKzt);
    }
}
