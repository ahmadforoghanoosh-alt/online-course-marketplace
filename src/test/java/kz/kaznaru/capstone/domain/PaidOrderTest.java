package kz.kaznaru.capstone.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class PaidOrderTest {
    @Test
    void paidOrderKeepsACopyOfEachCourse() {
        Course java = Course.builder().id(1).title("Java").instructor("A").price(15000).build();
        Order order = new Order("ORD-1", new Student("Ali"), List.of(java), 13500);

        PaidOrder saved = PaidOrder.of(order);

        assertEquals("ORD-1", saved.reference());
        assertEquals("Ali", saved.student());
        assertEquals(13500, saved.total());
        assertEquals(new OrderLine(1, "Java", "A", 15000), saved.lines().get(0));
        assertEquals("Java", saved.lines().get(0).toCourse().title());
    }
}
