package kz.kaznaru.capstone.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Builder: named steps, safe defaults, and the same checks as the Course constructor. */
class CourseBuilderTest {

    @Test
    void buildsACourseWithNamedValues() {
        Course course = Course.builder()
                .id(7)
                .title("Java Basics")
                .instructor("A. Nurlanov")
                .description("Loops and classes")
                .price(15000)
                .build();

        assertEquals(new Course(7, "Java Basics", "A. Nurlanov", "Loops and classes", 15000), course);
    }

    @Test
    void optionalPartsGetDefaults() {
        Course course = Course.builder().title("SQL").price(12000).build();

        assertEquals(0, course.id());
        assertEquals("Unknown instructor", course.instructor());
        assertEquals("", course.description());
    }

    @Test
    void blankInstructorAndNullDescriptionUseDefaults() {
        Course course = Course.builder().title("Git").instructor(" ").description(null).price(5000).build();

        assertEquals("Unknown instructor", course.instructor());
        assertEquals("", course.description());
    }

    @Test
    void requiredPartsAreChecked() {
        assertThrows(IllegalArgumentException.class, () -> Course.builder().title("Java").build());
        assertThrows(IllegalArgumentException.class, () -> Course.builder().price(100).build());
        assertThrows(IllegalArgumentException.class, () -> Course.builder().title("Java").price(0).build());
    }
}
