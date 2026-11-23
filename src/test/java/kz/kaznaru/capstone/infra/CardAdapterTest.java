package kz.kaznaru.capstone.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CardAdapterTest {

    static class RecordingCardSdk extends CardSdk {
        String lastCurrency;
        double lastAmount;

        @Override
        public int authorize(String currency, double amount, String description) {
            lastCurrency = currency;
            lastAmount = amount;
            return super.authorize(currency, amount, description);
        }
    }

    @Test
    void sendsKztAndAmount() {
        RecordingCardSdk sdk = new RecordingCardSdk();
        assertTrue(new CardAdapter(sdk).charge(2500.0, "ORD-2"));
        assertEquals("KZT", sdk.lastCurrency);
        assertEquals(2500.0, sdk.lastAmount);
    }

    @Test
    void declinedMeansFalse() {
        assertFalse(new CardAdapter(new CardSdk()).charge(-1, "ORD-2"));
    }
}
