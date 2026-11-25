package kz.kaznaru.capstone.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CartTest {
    private final Course java = new Course(1, "Java", "A", "d", 15000);
    private final Course sql = new Course(2, "SQL", "B", "d", 12000);

    @Test
    void emptyCartHasTotalZero() {
        Cart cart = new Cart();
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.total());
    }

    @Test
    void totalIsSumOfPrices() {
        Cart cart = new Cart();
        cart.add(java);
        cart.add(sql);
        assertEquals(27000, cart.total());
    }

    @Test
    void sameCourseCanNotBeAddedTwice() {
        Cart cart = new Cart();
        cart.add(java);
        assertThrows(IllegalStateException.class, () -> cart.add(java));
    }

    @Test
    void removeTakesCourseOut() {
        Cart cart = new Cart();
        cart.add(java);
        cart.remove(1);
        assertFalse(cart.contains(1));
        assertThrows(IllegalArgumentException.class, () -> cart.remove(1));
    }

    @Test
    void clearEmptiesCart() {
        Cart cart = new Cart();
        cart.add(java);
        cart.clear();
        assertTrue(cart.isEmpty());
    }

    @Test
    void discountChangesTotalButNotSubtotal() {
        Cart cart = new Cart();
        cart.add(java);
        cart.add(sql);
        cart.applyDiscount(new PercentDiscount(10));
        assertEquals(27000, cart.subtotal());
        assertEquals(24300, cart.total());
        assertTrue(cart.hasDiscount());
    }

    @Test
    void newDiscountReplacesOldOne() {
        Cart cart = new Cart();
        cart.add(java);
        cart.applyDiscount(new PercentDiscount(10));
        cart.applyDiscount(new FixedAmountDiscount(1000));
        assertEquals(14000, cart.total());
    }

    @Test
    void clearAlsoRemovesDiscount() {
        Cart cart = new Cart();
        cart.add(java);
        cart.applyDiscount(new PercentDiscount(10));
        cart.clear();
        assertFalse(cart.hasDiscount());
    }
}
