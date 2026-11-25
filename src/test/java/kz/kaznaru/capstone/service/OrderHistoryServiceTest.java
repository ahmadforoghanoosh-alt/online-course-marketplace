package kz.kaznaru.capstone.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.FakeOrderRepository;
import kz.kaznaru.capstone.domain.Order;
import kz.kaznaru.capstone.domain.Student;
import org.junit.jupiter.api.Test;

class OrderHistoryServiceTest {
    private final FakeOrderRepository repo = new FakeOrderRepository();
    private final OrderHistoryService history = new OrderHistoryService(repo);
    private final Course java = Course.builder().id(1).title("Java").instructor("A").price(15000).build();

    private static PaymentStrategy paying(boolean result) {
        return new PaymentStrategy() {
            @Override
            public String name() {
                return "Test";
            }

            @Override
            public boolean pay(Order order) {
                return result;
            }
        };
    }

    private Order buy(Student student, boolean paymentWorks) {
        CheckoutService checkout = new CheckoutService(history.nextOrderNumber());
        checkout.subscribe(history);
        Cart cart = new Cart();
        cart.add(java);
        return checkout.checkout(student, cart, paying(paymentWorks));
    }

    @Test
    void paidOrderIsSavedByTheObserver() {
        buy(new Student("Ali"), true);

        assertEquals(1, history.history().size());
        assertEquals("ORD-1", history.history().get(0).reference());
        assertEquals(2, history.nextOrderNumber());
    }

    @Test
    void failedPaymentIsNotSaved() {
        buy(new Student("Ali"), false);

        assertTrue(history.history().isEmpty());
    }

    @Test
    void nextOrderGetsANewNumberAfterARestart() {
        buy(new Student("Ali"), true);

        Order second = buy(new Student("Ali"), true);   // a new CheckoutService, like after a restart

        assertEquals("ORD-2", second.getId());
    }

    @Test
    void myCoursesComeBackAfterARestart() {
        buy(new Student("Ali"), true);

        Student afterRestart = new Student("Ali");
        history.restoreEnrollments(afterRestart);
        Student other = new Student("Dana");
        history.restoreEnrollments(other);

        assertEquals("Java", afterRestart.getEnrolled().get(0).title());
        assertTrue(other.getEnrolled().isEmpty());
    }
}
