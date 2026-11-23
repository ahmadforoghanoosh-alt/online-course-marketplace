package kz.kaznaru.capstone.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.Order;
import kz.kaznaru.capstone.domain.Student;
import org.junit.jupiter.api.Test;

class PaymentProcessorsTest {
    private final Order order = new Order("ORD-1", new Student("Ali"),
            List.of(new Course(1, "Java", "A", "d", 15000)));

    @Test
    void kaspiProcessorCreatesKaspiAdapter() {
        KaspiPaymentProcessor kaspi = new KaspiPaymentProcessor();
        assertInstanceOf(KaspiAdapter.class, kaspi.createGateway());
        assertEquals("Kaspi", kaspi.name());
        assertTrue(kaspi.pay(order));
    }

    @Test
    void cardProcessorCreatesCardAdapter() {
        CardPaymentProcessor card = new CardPaymentProcessor();
        assertInstanceOf(CardAdapter.class, card.createGateway());
        assertEquals("Bank card", card.name());
        assertTrue(card.pay(order));
    }
}
