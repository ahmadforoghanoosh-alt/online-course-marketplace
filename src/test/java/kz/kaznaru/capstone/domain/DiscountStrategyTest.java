package kz.kaznaru.capstone.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DiscountStrategyTest {

    @Test
    void percentTakesPartOfTotal() {
        assertEquals(13500, new PercentDiscount(10).apply(15000));
    }

    @Test
    void fixedAmountIsSubtracted() {
        assertEquals(13000, new FixedAmountDiscount(2000).apply(15000));
    }

    @Test
    void fixedAmountNeverGoesBelowZero() {
        assertEquals(0, new FixedAmountDiscount(2000).apply(1500));
    }

    @Test
    void badDiscountsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new PercentDiscount(0));
        assertThrows(IllegalArgumentException.class, () -> new PercentDiscount(101));
        assertThrows(IllegalArgumentException.class, () -> new FixedAmountDiscount(0));
        assertThrows(IllegalArgumentException.class, () -> new FixedAmountDiscount(Double.NaN));
    }

    @Test
    void describeShowsTheRule() {
        assertEquals("10% off", new PercentDiscount(10).describe());
        assertEquals("2000 KZT off", new FixedAmountDiscount(2000).describe());
    }
}
