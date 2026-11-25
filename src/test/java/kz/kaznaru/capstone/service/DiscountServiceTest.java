package kz.kaznaru.capstone.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.FixedAmountDiscount;
import kz.kaznaru.capstone.domain.PercentDiscount;
import org.junit.jupiter.api.Test;

class DiscountServiceTest {
    private final DiscountService service = new DiscountService(Map.of(
            "STUDENT10", new PercentDiscount(10),
            "WELCOME2000", new FixedAmountDiscount(2000)));

    private Cart cartWithJava() {
        Cart cart = new Cart();
        cart.add(new Course(1, "Java", "A", "d", 15000));
        return cart;
    }

    @Test
    void validCodeLowersTotalByItsRule() {
        Cart cart = cartWithJava();
        service.applyCode(cart, "STUDENT10");
        assertEquals(13500, cart.total());
    }

    @Test
    void codeIsNotCaseSensitive() {
        Cart cart = cartWithJava();
        service.applyCode(cart, "  welcome2000 ");
        assertEquals(13000, cart.total());
    }

    @Test
    void invalidCodeShowsMessageAndTotalDoesNotChange() {
        Cart cart = cartWithJava();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> service.applyCode(cart, "FREE"));
        assertEquals("Unknown discount code: FREE", e.getMessage());
        assertEquals(15000, cart.total());
        assertFalse(cart.hasDiscount());
    }

    @Test
    void emptyCartCanNotGetDiscount() {
        assertThrows(IllegalStateException.class, () -> service.applyCode(new Cart(), "STUDENT10"));
    }
}
