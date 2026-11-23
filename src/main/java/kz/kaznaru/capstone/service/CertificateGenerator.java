package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.Student;
import java.time.LocalDate;

/**
 * Template Method: the steps of a certificate are always the same
 * (check, header, body, footer). Subclasses only change the format.
 */
public abstract class CertificateGenerator {

    public final String generate(Student student, Course course, LocalDate date) {
        if (!student.hasCompleted(course)) {
            throw new IllegalStateException("Course " + course.title() + " is not completed yet");
        }
        return header() + body(student, course, date) + footer();
    }

    protected abstract String header();

    protected abstract String body(Student student, Course course, LocalDate date);

    protected abstract String footer();
}
