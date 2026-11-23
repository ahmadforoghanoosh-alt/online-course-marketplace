package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Order;
import kz.kaznaru.capstone.domain.PaymentGateway;

/**
 * Factory Method: each subclass decides which PaymentGateway to create.
 * Checkout only works with PaymentProcessor, so a new payment type is a new subclass.
 */
public abstract class PaymentProcessor {

    protected abstract PaymentGateway createGateway();

    public abstract String name();

    public boolean pay(Order order) {
        PaymentGateway gateway = createGateway();
        return gateway.charge(order.getTotal(), order.getId());
    }
}
