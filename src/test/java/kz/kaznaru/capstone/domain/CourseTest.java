package kz.kaznaru.capstone.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CourseTest {
    @Test
    void blankTitleIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Course(1, " ", "A", "d", 100));
    }

    @Test
    void priceMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> new Course(1, "Java", "A", "d", 0));
    }

    @Test
    void notANumberPriceIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Course(1, "Java", "A", "d", Double.NaN));
        assertThrows(IllegalArgumentException.class,
                () -> new Course(1, "Java", "A", "d", Double.POSITIVE_INFINITY));
    }
}
