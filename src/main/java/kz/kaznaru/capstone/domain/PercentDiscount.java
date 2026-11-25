package kz.kaznaru.capstone.domain;

/** Takes a percent off the total, for example 10%. */
public class PercentDiscount implements DiscountStrategy {
    private final int percent;

    public PercentDiscount(int percent) {
        if (percent <= 0 || percent > 100) {
            throw new IllegalArgumentException("percent must be from 1 to 100");
        }
        this.percent = percent;
    }

    @Override
    public double apply(double total) {
        return total - total * percent / 100.0;
    }

    @Override
    public String describe() {
        return percent + "% off";
    }
}
