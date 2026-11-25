package kz.kaznaru.capstone.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.Student;
import org.junit.jupiter.api.Test;

/** Chain of Responsibility (ADR-0012): one test per check, then the whole chain. No database needed. */
class CheckoutCheckTest {
    private final Course java = new Course(1, "Java Basics", "A", "d", 15000);
    private final Course sql = new Course(2, "SQL", "B", "d", 12000);
    private final PaymentStrategy kaspi = fakePayment("Kaspi");
    private final PaymentStrategy card = fakePayment("Bank card");

    private static PaymentStrategy fakePayment(String name) {
        return new PaymentStrategy() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public boolean pay(kz.kaznaru.capstone.domain.Order order) {
                return true;
            }
        };
    }

    private Cart cartWith(Course... courses) {
        Cart cart = new Cart();
        for (Course c : courses) {
            cart.add(c);
        }
        return cart;
    }

    private static String failure(CheckoutCheck check, Student s, Cart c, PaymentStrategy p) {
        return assertThrows(IllegalStateException.class, () -> check.check(s, c, p)).getMessage();
    }

    // ---- one test per check ----

    @Test
    void cartNotEmptyStopsAnEmptyCart() {
        CheckoutCheck check = new CartNotEmptyCheck();
        assertEquals("Cart is empty", failure(check, new Student("Ali"), new Cart(), kaspi));
        assertDoesNotThrow(() -> check.check(new Student("Ali"), cartWith(java), kaspi));
    }

    @Test
    void notAlreadyBoughtStopsACourseThatIsInMyCourses() {
        Student ali = new Student("Ali");
        ali.enroll(java);
        CheckoutCheck check = new NotAlreadyBoughtCheck();
        assertEquals("You already have \"Java Basics\" in My courses", failure(check, ali, cartWith(sql, java), kaspi));
        assertDoesNotThrow(() -> check.check(ali, cartWith(sql), kaspi));
    }

    @Test
    void paymentMethodMustBeOneTheShopOffers() {
        CheckoutCheck check = new PaymentMethodCheck(List.of(kaspi, card));
        Student ali = new Student("Ali");
        assertEquals("Choose a payment method", failure(check, ali, cartWith(java), null));
        assertEquals("Choose a payment method", failure(check, ali, cartWith(java), fakePayment("Bitcoin")));
        assertDoesNotThrow(() -> check.check(ali, cartWith(java), card));
    }

    @Test
    void maxTotalStopsAnOrderAboveTheLimit() {
        CheckoutCheck check = new MaxTotalCheck(20000);
        Student ali = new Student("Ali");
        assertEquals("Order total is above the limit of 20000.00 KZT", failure(check, ali, cartWith(java, sql), kaspi));
        assertDoesNotThrow(() -> check.check(ali, cartWith(java), kaspi));
        assertThrows(IllegalArgumentException.class, () -> new MaxTotalCheck(0));
    }

    // ---- the whole chain ----

    /** A check that only writes down that it was reached, to see the order of the chain. */
    private static CheckoutCheck recorder(List<String> log, String name) {
        return new CheckoutCheck() {
            @Override
            protected void verify(Student student, Cart cart, PaymentStrategy payment) {
                log.add(name);
            }
        };
    }

    private CheckoutCheck fullChain() {
        CheckoutCheck first = new CartNotEmptyCheck();
        first.setNext(new NotAlreadyBoughtCheck())
                .setNext(new PaymentMethodCheck(List.of(kaspi, card)))
                .setNext(new MaxTotalCheck(100000));
        return first;
    }

    @Test
    void validOrderPassesTheWholeChain() {
        assertDoesNotThrow(() -> fullChain().check(new Student("Ali"), cartWith(java, sql), kaspi));
    }

    @Test
    void chainStopsAtTheFirstFailedCheck() {
        // empty cart AND no payment method: only the first message is shown
        assertEquals("Cart is empty", failure(fullChain(), new Student("Ali"), new Cart(), null));

        Student ali = new Student("Ali");
        ali.enroll(java);
        // already bought AND no payment method: the second check stops it first
        assertEquals("You already have \"Java Basics\" in My courses", failure(fullChain(), ali, cartWith(java), null));
    }

    @Test
    void checksRunInTheOrderTheyAreLinked() {
        List<String> log = new ArrayList<>();
        CheckoutCheck first = recorder(log, "1");
        first.setNext(recorder(log, "2")).setNext(recorder(log, "3"));

        first.check(new Student("Ali"), cartWith(java), kaspi);

        assertEquals(List.of("1", "2", "3"), log);
    }
}
