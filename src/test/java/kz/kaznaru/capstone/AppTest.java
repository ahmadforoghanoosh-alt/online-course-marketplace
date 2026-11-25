package kz.kaznaru.capstone;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import kz.kaznaru.capstone.domain.CourseRepository;
import kz.kaznaru.capstone.infra.InMemoryCourseRepository;
import org.junit.jupiter.api.Test;

class AppTest {
    @Test
    void memoryFlagChoosesInMemoryRepository() {
        CourseRepository repo = App.chooseRepository(new String[] {"--memory"});

        assertInstanceOf(InMemoryCourseRepository.class, repo);
    }
}
