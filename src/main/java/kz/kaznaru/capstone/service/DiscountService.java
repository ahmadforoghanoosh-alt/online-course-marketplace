package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.DiscountStrategy;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Use case: apply a discount code to the cart (US-07).
 * Knows which code means which DiscountStrategy; the cart only knows the strategy.
 */
public class DiscountService {
    private final Map<String, DiscountStrategy> codes = new HashMap<>();

    public DiscountService(Map<String, DiscountStrategy> codes) {
        codes.forEach((code, strategy) -> this.codes.put(normalize(code), strategy));
    }

    public DiscountStrategy applyCode(Cart cart, String code) {
        if (cart.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }
        DiscountStrategy strategy = codes.get(normalize(code));
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown discount code: " + code);
        }
        cart.applyDiscount(strategy);
        return strategy;
    }

    private static String normalize(String code) {
        return code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
    }
}
