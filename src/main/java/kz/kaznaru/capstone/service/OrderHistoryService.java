package kz.kaznaru.capstone.service;

import java.util.List;
import kz.kaznaru.capstone.domain.Order;
import kz.kaznaru.capstone.domain.OrderLine;
import kz.kaznaru.capstone.domain.OrderListener;
import kz.kaznaru.capstone.domain.OrderRepository;
import kz.kaznaru.capstone.domain.PaidOrder;
import kz.kaznaru.capstone.domain.Student;

/**
 * Observer: saves every paid order (ADR-0010). CheckoutService did not change: it only got one more listener.
 * It also gives the order history back and restores "My courses" after the app is restarted.
 */
public class OrderHistoryService implements OrderListener {
    private final OrderRepository orders;

    public OrderHistoryService(OrderRepository orders) {
        this.orders = orders;
    }

    @Override
    public void onOrderPaid(Order order) {
        orders.save(PaidOrder.of(order));
    }

    public List<PaidOrder> history() {
        return orders.findAll();
    }

    /** The number for the next order, so references stay unique after a restart. */
    public int nextOrderNumber() {
        return orders.findAll().size() + 1;
    }

    /** Enrolls the student again in every course from his saved orders. */
    public void restoreEnrollments(Student student) {
        for (PaidOrder order : orders.findAll()) {
            if (order.student().equals(student.getName())) {
                for (OrderLine line : order.lines()) {
                    student.enroll(line.toCourse());
                }
            }
        }
    }
}
