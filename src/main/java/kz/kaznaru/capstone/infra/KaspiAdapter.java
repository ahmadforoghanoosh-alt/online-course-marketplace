package kz.kaznaru.capstone.infra;

import kz.kaznaru.capstone.domain.PaymentGateway;

/** Adapter: makes KaspiSdk look like our PaymentGateway. */
public class KaspiAdapter implements PaymentGateway {
    private final KaspiSdk sdk;

    public KaspiAdapter(KaspiSdk sdk) {
        this.sdk = sdk;
    }

    @Override
    public boolean charge(double amountKzt, String reference) {
        long tiyn = Math.round(amountKzt * 100);
        return sdk.sendPayment(tiyn, reference).equals("OK");
    }
}
