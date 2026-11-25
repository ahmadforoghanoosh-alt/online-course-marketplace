package kz.kaznaru.capstone.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.DiscountStrategy;
import kz.kaznaru.capstone.domain.FakeCourseRepository;
import kz.kaznaru.capstone.domain.FixedAmountDiscount;
import kz.kaznaru.capstone.domain.PercentDiscount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** US-05: after undo the cart is exactly the same as before the last change. */
class CartHistoryTest {
    private Cart cart;
    private CartService cartService;
    private DiscountService discounts;
    private CartHistory history;

    @BeforeEach
    void setUp() {
        FakeCourseRepository repo = new FakeCourseRepository();
        repo.add("Java", "A", "d", 15000);
        repo.add("SQL", "B", "d", 12000);
        repo.add("Git", "C", "d", 5000);
        cart = new Cart();
        cartService = new CartService(repo);
        discounts = new DiscountService(Map.of("STUDENT10", new PercentDiscount(10),
                "WELCOME2000", new FixedAmountDiscount(2000)));
        history = new CartHistory();
    }

    @Test
    void undoAddRemovesTheCourse() {
        history.run(new AddToCartCommand(cartService, cart, 1));

        CartCommand undone = history.undo();

        assertTrue(cart.isEmpty());
        assertEquals("add Java", undone.describe());
    }

    @Test
    void undoRemovePutsTheCourseBackAtTheSamePlace() {
        history.run(new AddToCartCommand(cartService, cart, 1));
        history.run(new AddToCartCommand(cartService, cart, 2));
        history.run(new AddToCartCommand(cartService, cart, 3));
        List<Course> before = cart.getCourses();

        history.run(new RemoveFromCartCommand(cart, 2));
        assertFalse(cart.contains(2));
        history.undo();

        assertEquals(before, cart.getCourses());
        assertEquals(32000, cart.total());
    }

    @Test
    void undoDiscountBringsBackTheOldDiscount() {
        history.run(new AddToCartCommand(cartService, cart, 1));
        history.run(new ApplyDiscountCommand(discounts, cart, "STUDENT10"));
        DiscountStrategy first = cart.getDiscount();
        history.run(new ApplyDiscountCommand(discounts, cart, "WELCOME2000"));

        history.undo();
        assertSame(first, cart.getDiscount());
        assertEquals(13500, cart.total());

        history.undo();
        assertNull(cart.getDiscount());
        assertEquals(15000, cart.total());
    }

    @Test
    void severalUndosGoBackStepByStep() {
        history.run(new AddToCartCommand(cartService, cart, 1));
        history.run(new AddToCartCommand(cartService, cart, 2));
        history.run(new RemoveFromCartCommand(cart, 1));

        history.undo();
        assertEquals(27000, cart.total());
        history.undo();
        assertEquals(15000, cart.total());
        history.undo();
        assertTrue(cart.isEmpty());
        assertFalse(history.canUndo());
    }

    @Test
    void failedCommandIsNotSaved() {
        history.run(new AddToCartCommand(cartService, cart, 1));

        assertThrows(IllegalArgumentException.class,
                () -> history.run(new AddToCartCommand(cartService, cart, 99)));
        assertThrows(IllegalArgumentException.class,
                () -> history.run(new RemoveFromCartCommand(cart, 2)));
        history.undo();

        assertTrue(cart.isEmpty());
        assertFalse(history.canUndo());
    }

    @Test
    void nothingToUndo() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> history.undo());
        assertEquals("Nothing to undo", e.getMessage());
    }

    @Test
    void clearForgetsTheHistory() {
        history.run(new AddToCartCommand(cartService, cart, 1));

        history.clear();

        assertFalse(history.canUndo());
    }
}
