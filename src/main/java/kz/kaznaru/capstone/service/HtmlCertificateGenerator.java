package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.Student;
import java.time.LocalDate;

public class HtmlCertificateGenerator extends CertificateGenerator {
    @Override
    protected String header() {
        return "<html><body><h1>Certificate of Completion</h1>\n";
    }

    @Override
    protected String body(Student student, Course course, LocalDate date) {
        return "<p>This is to certify that <b>" + student.getName() + "</b></p>\n"
                + "<p>has completed the course <b>" + course.title() + "</b></p>\n"
                + "<p>Date: " + date + "</p>\n";
    }

    @Override
    protected String footer() {
        return "<p>Online Course Marketplace</p></body></html>\n";
    }
}
