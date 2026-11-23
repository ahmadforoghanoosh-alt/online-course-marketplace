package kz.kaznaru.capstone.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import kz.kaznaru.capstone.domain.Course;
import org.junit.jupiter.api.Test;

class InMemoryCourseRepositoryTest {
    @Test
    void addedCourseGetsIdAndCanBeFound() {
        InMemoryCourseRepository repo = new InMemoryCourseRepository();
        Course first = repo.add("Java", "A", "d", 15000);
        Course second = repo.add("SQL", "B", "d", 12000);

        assertEquals(1, first.id());
        assertEquals(2, second.id());
        assertEquals("SQL", repo.findById(2).orElseThrow().title());
        assertEquals(2, repo.findAll().size());
        assertTrue(repo.findById(99).isEmpty());
    }
}
