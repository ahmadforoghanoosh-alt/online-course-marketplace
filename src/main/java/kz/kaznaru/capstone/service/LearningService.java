package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.Student;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Facade for "My courses" and certificates (US-10).
 * The UI calls one simple method; the Facade hides the steps:
 * find the course among the student's courses, check the format, mark it completed, build the certificate.
 */
public class LearningService {
    private final Map<String, CertificateGenerator> generators;

    public LearningService(Map<String, CertificateGenerator> generators) {
        if (generators.isEmpty()) {
            throw new IllegalArgumentException("at least one certificate format is needed");
        }
        this.generators = new LinkedHashMap<>(generators);
    }

    public List<Course> myCourses(Student student) {
        return student.getEnrolled();
    }

    /** The rule that used to live in ConsoleMenu: you can only finish a course you bought. */
    public Course findMyCourse(Student student, long courseId) {
        return student.getEnrolled().stream()
                .filter(c -> c.id() == courseId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("This course is not in My courses"));
    }

    public List<String> certificateFormats() {
        return List.copyOf(generators.keySet());
    }

    /** Checks everything first, and only then marks the course completed and builds the certificate. */
    public String completeAndCertify(Student student, long courseId, String format, LocalDate date) {
        Course course = findMyCourse(student, courseId);
        CertificateGenerator generator = generators.get(format);
        if (generator == null) {
            throw new IllegalArgumentException("Unknown format");
        }
        student.complete(course);
        return generator.generate(student, course, date);
    }
}
