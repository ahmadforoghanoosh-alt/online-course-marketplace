package kz.kaznaru.capstone.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class KaspiAdapterTest {

    /** Remembers what the adapter sent to the SDK. */
    static class RecordingKaspiSdk extends KaspiSdk {
        long lastTiyn;
        String lastRef;

        @Override
        public String sendPayment(long amountTiyn, String orderRef) {
            lastTiyn = amountTiyn;
            lastRef = orderRef;
            return super.sendPayment(amountTiyn, orderRef);
        }
    }

    @Test
    void convertsTengeToTiyn() {
        RecordingKaspiSdk sdk = new RecordingKaspiSdk();
        boolean ok = new KaspiAdapter(sdk).charge(1500.0, "ORD-1");

        assertTrue(ok);
        assertEquals(150000, sdk.lastTiyn);
        assertEquals("ORD-1", sdk.lastRef);
    }

    @Test
    void errorFromSdkMeansFalse() {
        assertFalse(new KaspiAdapter(new KaspiSdk()).charge(0, "ORD-1"));
    }
}
