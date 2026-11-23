package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.Student;
import java.time.LocalDate;

public class TextCertificateGenerator extends CertificateGenerator {
    @Override
    protected String header() {
        return "===== CERTIFICATE OF COMPLETION =====\n";
    }

    @Override
    protected String body(Student student, Course course, LocalDate date) {
        return "This is to certify that " + student.getName() + "\n"
                + "has completed the course \"" + course.title() + "\"\n"
                + "Date: " + date + "\n";
    }

    @Override
    protected String footer() {
        return "===== Online Course Marketplace =====\n";
    }
}
