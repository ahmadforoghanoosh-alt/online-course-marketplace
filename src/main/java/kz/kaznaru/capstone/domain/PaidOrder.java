package kz.kaznaru.capstone.domain;

import java.util.List;

/** A paid order as it is saved in the order history. */
public record PaidOrder(String reference, String student, double total, List<OrderLine> lines) {
    public PaidOrder {
        lines = List.copyOf(lines);
    }

    public static PaidOrder of(Order order) {
        return new PaidOrder(order.getId(), order.getStudent().getName(), order.getTotal(),
                order.getCourses().stream().map(OrderLine::of).toList());
    }
}
