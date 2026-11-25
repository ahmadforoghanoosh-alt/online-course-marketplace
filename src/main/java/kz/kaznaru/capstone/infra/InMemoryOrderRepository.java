package kz.kaznaru.capstone.infra;

import java.util.ArrayList;
import java.util.List;
import kz.kaznaru.capstone.domain.OrderRepository;
import kz.kaznaru.capstone.domain.PaidOrder;

/** Order history kept in memory (used with --memory). */
public class InMemoryOrderRepository implements OrderRepository {
    private final List<PaidOrder> orders = new ArrayList<>();

    @Override
    public void save(PaidOrder order) {
        orders.add(order);
    }

    @Override
    public List<PaidOrder> findAll() {
        return List.copyOf(orders);
    }
}
