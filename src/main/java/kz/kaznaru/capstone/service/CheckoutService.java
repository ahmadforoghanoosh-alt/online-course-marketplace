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
 */
public class CheckoutService {
    private final List<OrderListener> listeners = new ArrayList<>();
    private int nextOrderNumber = 1;

    public void subscribe(OrderListener listener) {
        listeners.add(listener);
    }

    public Order checkout(Student student, Cart cart, PaymentProcessor processor) {
        if (cart.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }
        Order order = new Order("ORD-" + nextOrderNumber, student, cart.getCourses(), cart.total());
        nextOrderNumber++;

        // A free order (discount covers everything) needs no payment provider.
        boolean paid = order.getTotal() == 0 || processor.pay(order);
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
