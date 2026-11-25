package kz.kaznaru.capstone.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.Student;
import org.junit.jupiter.api.Test;

class LearningServiceTest {
    private final Course java = new Course(1, "Java Basics", "A", "d", 15000);
    private final LocalDate date = LocalDate.of(2026, 11, 25);

    private LearningService service() {
        Map<String, CertificateGenerator> generators = new LinkedHashMap<>();
        generators.put("Text", new TextCertificateGenerator());
        generators.put("HTML", new HtmlCertificateGenerator());
        return new LearningService(generators);
    }

    private Student enrolledStudent() {
        Student s = new Student("Ali");
        s.enroll(java);
        return s;
    }

    @Test
    void completeAndCertifyMarksCourseAndReturnsCertificate() {
        Student s = enrolledStudent();

        String text = service().completeAndCertify(s, 1, "Text", date);

        assertTrue(s.hasCompleted(java));
        assertTrue(text.contains("Ali"));
        assertTrue(text.contains("Java Basics"));
        assertTrue(text.contains("2026-11-25"));
    }

    @Test
    void courseNotBoughtIsRejected() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> service().completeAndCertify(new Student("Ali"), 1, "Text", date));
        assertEquals("This course is not in My courses", e.getMessage());
    }

    @Test
    void unknownFormatDoesNotMarkCourseCompleted() {
        Student s = enrolledStudent();

        assertThrows(IllegalArgumentException.class, () -> service().completeAndCertify(s, 1, "PDF", date));

        assertFalse(s.hasCompleted(java));
    }

    @Test
    void formatsKeepTheirOrder() {
        assertEquals(List.of("Text", "HTML"), service().certificateFormats());
    }

    @Test
    void myCoursesAreTheEnrolledCourses() {
        assertEquals(List.of(java), service().myCourses(enrolledStudent()));
    }

    @Test
    void atLeastOneFormatIsNeeded() {
        assertThrows(IllegalArgumentException.class, () -> new LearningService(Map.of()));
    }
}
