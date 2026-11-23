package kz.kaznaru.capstone.domain;

/** Observer: is told when an order is paid. */
public interface OrderListener {
    void onOrderPaid(Order order);
}
