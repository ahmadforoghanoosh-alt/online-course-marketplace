package kz.kaznaru.capstone.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.Order;
import kz.kaznaru.capstone.domain.OrderStatus;
import kz.kaznaru.capstone.domain.PaymentGateway;
import kz.kaznaru.capstone.domain.Student;
import org.junit.jupiter.api.Test;

/** Strategy for the payment method: checkout works with any PaymentStrategy, chosen at run time. */
class PaymentStrategyTest {
    private final Course java = new Course(1, "Java", "A", "d", 15000);

    /** A new payment method written as ONE new class; CheckoutService does not change. */
    static class GiftCardPayment implements PaymentStrategy {
        final List<Order> charged = new ArrayList<>();

        @Override
        public String name() {
            return "Gift card";
        }

        @Override
        public boolean pay(Order order) {
            charged.add(order);
            return true;
        }
    }

    @Test
    void newPaymentMethodNeedsOnlyANewClass() {
        Cart cart = new Cart();
        cart.add(java);
        GiftCardPayment giftCard = new GiftCardPayment();

        Order order = new CheckoutService().checkout(new Student("Ali"), cart, giftCard);

        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(1, giftCard.charged.size());
        assertEquals(15000, giftCard.charged.get(0).getTotal());
    }

    @Test
    void theStudentCanSwitchStrategyBetweenOrders() {
        CheckoutService checkout = new CheckoutService();
        PaymentStrategy failing = new PaymentProcessor() {
            @Override
            protected PaymentGateway createGateway() {
                return (amount, ref) -> false;
            }

            @Override
            public String name() {
                return "Card (declined)";
            }
        };
        Cart cart = new Cart();
        cart.add(java);

        Order first = checkout.checkout(new Student("Ali"), cart, failing);
        Order second = checkout.checkout(new Student("Ali"), cart, new GiftCardPayment());

        assertEquals(OrderStatus.NEW, first.getStatus());
        assertEquals(OrderStatus.PAID, second.getStatus());
        assertTrue(cart.isEmpty());
    }

    @Test
    void processorsAreStrategies() {
        PaymentStrategy strategy = new PaymentProcessor() {
            @Override
            protected PaymentGateway createGateway() {
                return (amount, ref) -> true;
            }

            @Override
            public String name() {
                return "Kaspi";
            }
        };

        assertEquals("Kaspi", strategy.name());
    }
}
