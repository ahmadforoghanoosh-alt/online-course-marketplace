package kz.kaznaru.capstone.infra;

import kz.kaznaru.capstone.domain.PaymentGateway;
import kz.kaznaru.capstone.service.PaymentProcessor;

public class CardPaymentProcessor extends PaymentProcessor {
    @Override
    protected PaymentGateway createGateway() {
        return new CardAdapter(new CardSdk());
    }

    @Override
    public String name() {
        return "Bank card";
    }
}
