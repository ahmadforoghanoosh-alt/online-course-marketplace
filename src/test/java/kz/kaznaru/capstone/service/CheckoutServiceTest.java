package kz.kaznaru.capstone.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.FixedAmountDiscount;
import kz.kaznaru.capstone.domain.Order;
import kz.kaznaru.capstone.domain.OrderListener;
import kz.kaznaru.capstone.domain.OrderStatus;
import kz.kaznaru.capstone.domain.PaymentGateway;
import kz.kaznaru.capstone.domain.PercentDiscount;
import kz.kaznaru.capstone.domain.Student;
import org.junit.jupiter.api.Test;

class CheckoutServiceTest {
    private final Course java = new Course(1, "Java", "A", "d", 15000);
    private final Student student = new Student("Ali");

    /** Fake observer: counts how many times it was told. */
    static class FakeListener implements OrderListener {
        final List<Order> received = new ArrayList<>();

        @Override
        public void onOrderPaid(Order order) {
            received.add(order);
        }
    }

    private static PaymentProcessor processorThatReturns(boolean result) {
        return new PaymentProcessor() {
            @Override
            protected PaymentGateway createGateway() {
                return (amount, ref) -> result;
            }

            @Override
            public String name() {
                return "Fake";
            }
        };
    }

    @Test
    void successfulPaymentNotifiesAllListeners() {
        CheckoutService checkout = new CheckoutService();
        FakeListener first = new FakeListener();
        FakeListener second = new FakeListener();
        checkout.subscribe(first);
        checkout.subscribe(second);
        Cart cart = new Cart();
        cart.add(java);

        Order order = checkout.checkout(student, cart, processorThatReturns(true));

        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(1, first.received.size());
        assertEquals(1, second.received.size());
        assertTrue(cart.isEmpty());
    }

    @Test
    void failedPaymentDoesNotNotifyAndKeepsCart() {
        CheckoutService checkout = new CheckoutService();
        FakeListener listener = new FakeListener();
        checkout.subscribe(listener);
        Cart cart = new Cart();
        cart.add(java);

        Order order = checkout.checkout(student, cart, processorThatReturns(false));

        assertEquals(OrderStatus.NEW, order.getStatus());
        assertTrue(listener.received.isEmpty());
        assertFalse(cart.isEmpty());
    }

    @Test
    void emptyCartCanNotBeCheckedOut() {
        CheckoutService checkout = new CheckoutService();
        assertThrows(IllegalStateException.class,
                () -> checkout.checkout(student, new Cart(), processorThatReturns(true)));
    }

    @Test
    void enrollmentListenerEnrollsStudent() {
        CheckoutService checkout = new CheckoutService();
        checkout.subscribe(new EnrollmentListener());
        Cart cart = new Cart();
        cart.add(java);

        checkout.checkout(student, cart, processorThatReturns(true));

        assertTrue(student.isEnrolled(java));
    }

    @Test
    void orderUsesDiscountedTotal() {
        CheckoutService checkout = new CheckoutService();
        Cart cart = new Cart();
        cart.add(java);
        cart.applyDiscount(new PercentDiscount(10));

        Order order = checkout.checkout(student, cart, processorThatReturns(true));

        assertEquals(13500, order.getTotal());
    }

    @Test
    void freeOrderIsPaidWithoutCallingTheProvider() {
        CheckoutService checkout = new CheckoutService();
        Cart cart = new Cart();
        cart.add(java);
        cart.applyDiscount(new FixedAmountDiscount(20000));

        Order order = checkout.checkout(student, cart, processorThatReturns(false));

        assertEquals(0, order.getTotal());
        assertEquals(OrderStatus.PAID, order.getStatus());
    }
}
