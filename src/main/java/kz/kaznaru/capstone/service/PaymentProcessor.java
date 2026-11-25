package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Order;
import kz.kaznaru.capstone.domain.PaymentGateway;

/**
 * Factory Method: each subclass decides which PaymentGateway to create.
 * It is also a PaymentStrategy, so checkout can use any processor the student chooses
 * (Strategy + Factory Method working together, ADR-0007).
 */
public abstract class PaymentProcessor implements PaymentStrategy {

    protected abstract PaymentGateway createGateway();

    @Override
    public abstract String name();

    @Override
    public boolean pay(Order order) {
        PaymentGateway gateway = createGateway();
        return gateway.charge(order.getTotal(), order.getId());
    }
}
