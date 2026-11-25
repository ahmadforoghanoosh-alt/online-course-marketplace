package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Student;
import java.util.List;

/** Check 3: the chosen payment method is one that the shop offers. */
public class PaymentMethodCheck extends CheckoutCheck {
    private final List<PaymentStrategy> available;

    public PaymentMethodCheck(List<PaymentStrategy> available) {
        this.available = List.copyOf(available);
    }

    @Override
    protected void verify(Student student, Cart cart, PaymentStrategy payment) {
        if (payment == null || !available.contains(payment)) {
            throw new IllegalStateException("Choose a payment method");
        }
    }
}
