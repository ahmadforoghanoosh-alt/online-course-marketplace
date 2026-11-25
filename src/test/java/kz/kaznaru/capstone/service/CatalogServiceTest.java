package kz.kaznaru.capstone.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;
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

    @Test
    void courseCanBeEditedAndDeleted() {
        catalog.addCourse("Java", "A", "d", 15000);
        catalog.updateCourse(1, "Java Pro", "A", "d", 20000);
        assertEquals("Java Pro", catalog.findCourse(1).orElseThrow().title());

        catalog.deleteCourse(1);
        assertTrue(catalog.listCourses().isEmpty());
    }

    @Test
    void editOrDeleteOfUnknownCourseFails() {
        assertThrows(NoSuchElementException.class, () -> catalog.updateCourse(42, "X", "A", "d", 100));
        assertThrows(NoSuchElementException.class, () -> catalog.deleteCourse(42));
    }

    @Test
    void badEditIsRejected() {
        catalog.addCourse("Java", "A", "d", 15000);
        assertThrows(IllegalArgumentException.class, () -> catalog.updateCourse(1, "Java", "A", "d", 0));
        assertEquals(15000, catalog.findCourse(1).orElseThrow().price());
    }
}
