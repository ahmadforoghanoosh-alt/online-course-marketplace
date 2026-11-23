package kz.kaznaru.capstone.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import kz.kaznaru.capstone.domain.FakeCourseRepository;
import org.junit.jupiter.api.Test;

class CatalogServiceTest {
    private final CatalogService catalog = new CatalogService(new FakeCourseRepository());

    @Test
    void addedCourseIsListed() {
        catalog.addCourse("Java", "A", "d", 15000);
        assertEquals(1, catalog.listCourses().size());
    }

    @Test
    void badCourseIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> catalog.addCourse("", "A", "d", 100));
        assertThrows(IllegalArgumentException.class, () -> catalog.addCourse("Java", "A", "d", -5));
    }

    @Test
    void unknownIdIsEmpty() {
        assertTrue(catalog.findCourse(42).isEmpty());
    }
}
