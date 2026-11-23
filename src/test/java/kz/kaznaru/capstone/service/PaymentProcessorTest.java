package kz.kaznaru.capstone.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.Order;
import kz.kaznaru.capstone.domain.PaymentGateway;
import kz.kaznaru.capstone.domain.Student;
import org.junit.jupiter.api.Test;

class PaymentProcessorTest {

    /** Fake gateway: remembers the amount and reference. */
    static class FakeGateway implements PaymentGateway {
        double amount;
        String reference;

        @Override
        public boolean charge(double amountKzt, String ref) {
            amount = amountKzt;
            reference = ref;
            return true;
        }
    }

    @Test
    void payUsesTheGatewayCreatedBySubclass() {
        FakeGateway gateway = new FakeGateway();
        PaymentProcessor processor = new PaymentProcessor() {
            @Override
            protected PaymentGateway createGateway() {
                return gateway;
            }

            @Override
            public String name() {
                return "Fake";
            }
        };
        Order order = new Order("ORD-9", new Student("Ali"), List.of(new Course(1, "Java", "A", "d", 15000)));

        assertTrue(processor.pay(order));
        assertEquals(15000, gateway.amount);
        assertEquals("ORD-9", gateway.reference);
    }
}
