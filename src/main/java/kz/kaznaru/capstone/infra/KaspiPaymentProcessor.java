package kz.kaznaru.capstone.infra;

import kz.kaznaru.capstone.domain.PaymentGateway;
import kz.kaznaru.capstone.service.PaymentProcessor;

public class KaspiPaymentProcessor extends PaymentProcessor {
    @Override
    protected PaymentGateway createGateway() {
        return new KaspiAdapter(new KaspiSdk());
    }

    @Override
    public String name() {
        return "Kaspi";
    }
}
