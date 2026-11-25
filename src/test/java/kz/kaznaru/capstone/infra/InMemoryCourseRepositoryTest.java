package kz.kaznaru.capstone.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    @Test
    void courseCanBeUpdatedAndDeleted() {
        InMemoryCourseRepository repo = new InMemoryCourseRepository();
        repo.add("Java", "A", "d", 15000);

        Course changed = repo.update(1, "Java 2", "B", "new", 18000).orElseThrow();
        assertEquals("Java 2", changed.title());
        assertEquals(18000, repo.findById(1).orElseThrow().price());
        assertTrue(repo.update(99, "X", "B", "d", 100).isEmpty());

        assertTrue(repo.delete(1));
        assertTrue(repo.findAll().isEmpty());
        assertFalse(repo.delete(1));
    }
}
