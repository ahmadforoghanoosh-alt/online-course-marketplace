package kz.kaznaru.capstone;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import kz.kaznaru.capstone.domain.CourseRepository;
import kz.kaznaru.capstone.domain.OrderRepository;
import kz.kaznaru.capstone.infra.InMemoryCourseRepository;
import kz.kaznaru.capstone.infra.InMemoryOrderRepository;
import org.junit.jupiter.api.Test;

class AppTest {
    @Test
    void memoryFlagChoosesInMemoryRepository() {
        CourseRepository repo = App.chooseRepository(new String[] {"--memory"});

        assertInstanceOf(InMemoryCourseRepository.class, repo);
    }

    @Test
    void memoryFlagKeepsOrdersInMemoryToo() {
        OrderRepository orders = App.chooseOrderRepository(new String[] {"--memory"});

        assertInstanceOf(InMemoryOrderRepository.class, orders);
    }
}
