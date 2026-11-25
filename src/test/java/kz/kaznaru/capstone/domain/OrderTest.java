package kz.kaznaru.capstone.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class OrderTest {
    private final Student student = new Student("Ali");

    @Test
    void newOrderHasTotalAndStatusNew() {
        Order order = new Order("ORD-1", student, List.of(
                new Course(1, "Java", "A", "d", 15000), new Course(2, "SQL", "B", "d", 12000)));
        assertEquals(27000, order.getTotal());
        assertEquals(OrderStatus.NEW, order.getStatus());
        order.markPaid();
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void orderNeedsCourses() {
        assertThrows(IllegalArgumentException.class, () -> new Order("ORD-1", student, List.of()));
    }

    @Test
    void orderCanTakeDiscountedTotal() {
        Order order = new Order("ORD-2", student, List.of(new Course(1, "Java", "A", "d", 15000)), 13500);
        assertEquals(13500, order.getTotal());
        assertThrows(IllegalArgumentException.class,
                () -> new Order("ORD-3", student, List.of(new Course(1, "Java", "A", "d", 15000)), -1));
    }
}
