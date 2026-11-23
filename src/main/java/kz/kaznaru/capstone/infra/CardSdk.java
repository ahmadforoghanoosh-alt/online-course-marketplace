package kz.kaznaru.capstone.infra;

/**
 * Fake bank card SDK (we can not change it). It needs a currency code
 * and answers with an HTTP-like status number. No network.
 */
public class CardSdk {
    public static final int APPROVED = 200;
    public static final int DECLINED = 402;

    public int authorize(String currency, double amount, String description) {
        if (!"KZT".equals(currency) || amount <= 0) {
            return DECLINED;
        }
        return APPROVED;
    }
}
