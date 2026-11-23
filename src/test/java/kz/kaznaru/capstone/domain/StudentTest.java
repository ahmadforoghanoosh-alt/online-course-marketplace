package kz.kaznaru.capstone.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StudentTest {
    private final Course java = new Course(1, "Java", "A", "d", 15000);

    @Test
    void blankNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Student(""));
    }

    @Test
    void enrollTwiceKeepsOneCourse() {
        Student s = new Student("Ali");
        s.enroll(java);
        s.enroll(java);
        assertEquals(1, s.getEnrolled().size());
    }

    @Test
    void canCompleteOnlyEnrolledCourse() {
        Student s = new Student("Ali");
        assertThrows(IllegalStateException.class, () -> s.complete(java));
        s.enroll(java);
        s.complete(java);
        assertTrue(s.hasCompleted(java));
    }
}
