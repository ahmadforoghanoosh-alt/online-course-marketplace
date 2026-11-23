package kz.kaznaru.capstone.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.Student;
import org.junit.jupiter.api.Test;

class CertificateGeneratorTest {
    private final Course java = new Course(1, "Java Basics", "A", "d", 15000);
    private final LocalDate date = LocalDate.of(2026, 11, 20);

    private Student studentWhoFinished() {
        Student s = new Student("Ali");
        s.enroll(java);
        s.complete(java);
        return s;
    }

    @Test
    void textCertificateHasAllParts() {
        String text = new TextCertificateGenerator().generate(studentWhoFinished(), java, date);
        assertTrue(text.startsWith("===== CERTIFICATE OF COMPLETION ====="));
        assertTrue(text.contains("Ali"));
        assertTrue(text.contains("Java Basics"));
        assertTrue(text.contains("2026-11-20"));
        assertTrue(text.trim().endsWith("===== Online Course Marketplace ====="));
    }

    @Test
    void htmlCertificateHasAllParts() {
        String html = new HtmlCertificateGenerator().generate(studentWhoFinished(), java, date);
        assertTrue(html.startsWith("<html>"));
        assertTrue(html.contains("<b>Ali</b>"));
        assertTrue(html.contains("<b>Java Basics</b>"));
        assertTrue(html.trim().endsWith("</html>"));
    }

    @Test
    void notCompletedCourseHasNoCertificate() {
        Student s = new Student("Ali");
        s.enroll(java);
        assertThrows(IllegalStateException.class,
                () -> new TextCertificateGenerator().generate(s, java, date));
    }
}
