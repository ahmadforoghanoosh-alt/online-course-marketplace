package kz.kaznaru.capstone.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import kz.kaznaru.capstone.domain.OrderLine;
import kz.kaznaru.capstone.domain.PaidOrder;
import org.junit.jupiter.api.Test;

class InMemoryOrderRepositoryTest {
    @Test
    void savedOrdersAreReturnedInOrder() {
        InMemoryOrderRepository repo = new InMemoryOrderRepository();
        repo.save(new PaidOrder("ORD-1", "Ali", 15000, List.of(new OrderLine(1, "Java", "A", 15000))));
        repo.save(new PaidOrder("ORD-2", "Ali", 12000, List.of(new OrderLine(3, "SQL", "B", 12000))));

        assertEquals(2, repo.findAll().size());
        assertEquals("ORD-2", repo.findAll().get(1).reference());
    }
}
