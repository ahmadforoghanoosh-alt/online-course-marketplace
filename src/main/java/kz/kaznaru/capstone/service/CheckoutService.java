package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Order;
import kz.kaznaru.capstone.domain.OrderListener;
import kz.kaznaru.capstone.domain.Student;
import java.util.ArrayList;
import java.util.List;

/**
 * Subject in the Observer pattern: after a paid order it tells every listener.
 * It does not know what the listeners do (US-08, US-09).
 * Context in the Strategy pattern: the payment method is passed in as a PaymentStrategy.
 * Before payment the order goes through a Chain of Responsibility of CheckoutChecks (ADR-0012).
 */
public class CheckoutService {
    private final List<OrderListener> listeners = new ArrayList<>();
    private final CheckoutCheck checks;
    private int nextOrderNumber;

    public CheckoutService() {
        this(1);
    }

    /** @param firstOrderNumber number of the next order, so ORD-numbers do not repeat after a restart */
    public CheckoutService(int firstOrderNumber) {
        this(firstOrderNumber, new CartNotEmptyCheck());
    }

    /**
     * @param firstOrderNumber number of the next order
     * @param checks first check of the chain that every order must pass before payment (built in App)
     */
    public CheckoutService(int firstOrderNumber, CheckoutCheck checks) {
        this.nextOrderNumber = firstOrderNumber;
        this.checks = checks;
    }

    public void subscribe(OrderListener listener) {
        listeners.add(listener);
    }

    public Order checkout(Student student, Cart cart, PaymentStrategy payment) {
        checks.check(student, cart, payment);   // Chain of Responsibility: stops with a clear message
        Order order = new Order("ORD-" + nextOrderNumber, student, cart.getCourses(), cart.total());
        nextOrderNumber++;

        // A free order (discount covers everything) needs no payment provider.
        boolean paid = order.getTotal() == 0 || payment.pay(order);
        if (paid) {
            order.markPaid();
            cart.clear();
            for (OrderListener listener : listeners) {
                listener.onOrderPaid(order);
            }
        }
        return order;
    }
}
