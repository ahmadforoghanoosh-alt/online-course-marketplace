package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Student;

/** Check 1: there is something to pay for. */
public class CartNotEmptyCheck extends CheckoutCheck {
    @Override
    protected void verify(Student student, Cart cart, PaymentStrategy payment) {
        if (cart.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }
    }
}
