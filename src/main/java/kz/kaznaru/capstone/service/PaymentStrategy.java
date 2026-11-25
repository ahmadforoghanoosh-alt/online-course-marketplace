package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Order;

/**
 * Strategy pattern for the payment method (US-08): the student chooses one at checkout,
 * and CheckoutService only knows this interface.
 * Kaspi and card are PaymentProcessor subclasses, which use Factory Method to create their gateway.
 */
public interface PaymentStrategy {
    /** The name shown in the menu, for example "Kaspi". */
    String name();

    /** Tries to take the money for the order. Returns true if the payment worked. */
    boolean pay(Order order);
}
