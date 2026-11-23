package kz.kaznaru.capstone.domain;

/** What our code expects from any payment provider (Adapter target). */
public interface PaymentGateway {
    boolean charge(double amountKzt, String reference);
}
