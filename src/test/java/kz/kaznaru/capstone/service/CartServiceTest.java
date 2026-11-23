package kz.kaznaru.capstone.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.CourseRepository;
import kz.kaznaru.capstone.domain.FakeCourseRepository;
import org.junit.jupiter.api.Test;

class CartServiceTest {
    private final CourseRepository repo = new FakeCourseRepository();
    private final CartService service = new CartService(repo);

    @Test
    void addAndRemoveChangeTotal() {
        repo.add("Java", "A", "d", 15000);
        repo.add("SQL", "B", "d", 12000);
        Cart cart = new Cart();

        service.addToCart(cart, 1);
        service.addToCart(cart, 2);
        assertEquals(27000, service.total(cart));

        service.removeFromCart(cart, 1);
        assertEquals(12000, service.total(cart));
    }

    @Test
    void unknownCourseCanNotBeAdded() {
        Cart cart = new Cart();
        assertThrows(IllegalArgumentException.class, () -> service.addToCart(cart, 7));
        assertTrue(cart.isEmpty());
    }
}
