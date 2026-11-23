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
}
