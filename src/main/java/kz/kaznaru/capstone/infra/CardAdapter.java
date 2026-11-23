package kz.kaznaru.capstone.infra;

import kz.kaznaru.capstone.domain.PaymentGateway;

/** Adapter: makes CardSdk look like our PaymentGateway. */
public class CardAdapter implements PaymentGateway {
    private final CardSdk sdk;

    public CardAdapter(CardSdk sdk) {
        this.sdk = sdk;
    }

    @Override
    public boolean charge(double amountKzt, String reference) {
        return sdk.authorize("KZT", amountKzt, "Order " + reference) == CardSdk.APPROVED;
    }
}
