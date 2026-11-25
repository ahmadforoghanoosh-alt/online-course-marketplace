package kz.kaznaru.capstone.domain;

import java.util.List;

/** Port for the order history; implemented in infra (Repository pattern, like CourseRepository). */
public interface OrderRepository {
    void save(PaidOrder order);

    /** All saved orders, oldest first. */
    List<PaidOrder> findAll();
}
