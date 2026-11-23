package kz.kaznaru.capstone.infra;

/**
 * Fake vendor SDK (we can not change it). It works in tiyn (1 KZT = 100 tiyn)
 * and answers with a text code. No network.
 */
public class KaspiSdk {
    public String sendPayment(long amountTiyn, String orderRef) {
        if (amountTiyn <= 0 || orderRef == null || orderRef.isBlank()) {
            return "ERROR";
        }
        return "OK";
    }
}
