package kz.kaznaru.capstone.domain;

import java.util.ArrayList;
import java.util.List;

/** Order history for tests in service and ui (they may not use infra). */
public class FakeOrderRepository implements OrderRepository {
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
