package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Student;
import java.util.Locale;

/** Check 4: one order may not be larger than a limit (protects against mistakes, for example a wrong price). */
public class MaxTotalCheck extends CheckoutCheck {
    private final double maxTotal;

    public MaxTotalCheck(double maxTotal) {
        if (!(maxTotal > 0) || Double.isInfinite(maxTotal)) {
            throw new IllegalArgumentException("limit must be greater than 0");
        }
        this.maxTotal = maxTotal;
    }

    @Override
    protected void verify(Student student, Cart cart, PaymentStrategy payment) {
        if (cart.total() > maxTotal) {
            throw new IllegalStateException(String.format(Locale.ROOT,
                    "Order total is above the limit of %.2f KZT", maxTotal));
        }
    }
}
