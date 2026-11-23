package kz.kaznaru.capstone;

import kz.kaznaru.capstone.domain.CourseRepository;
import kz.kaznaru.capstone.domain.Student;
import kz.kaznaru.capstone.infra.CardPaymentProcessor;
import kz.kaznaru.capstone.infra.ConsoleReceiptNotifier;
import kz.kaznaru.capstone.infra.InMemoryCourseRepository;
import kz.kaznaru.capstone.infra.KaspiPaymentProcessor;
import kz.kaznaru.capstone.service.CartService;
import kz.kaznaru.capstone.service.CatalogService;
import kz.kaznaru.capstone.service.CertificateGenerator;
import kz.kaznaru.capstone.service.CheckoutService;
import kz.kaznaru.capstone.service.EnrollmentListener;
import kz.kaznaru.capstone.service.HtmlCertificateGenerator;
import kz.kaznaru.capstone.service.PaymentProcessor;
import kz.kaznaru.capstone.service.TextCertificateGenerator;
import kz.kaznaru.capstone.ui.ConsoleMenu;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/** Composition root: the ONLY place allowed to wire concrete classes together. */
public final class App {
    private App() { }

    public static void main(String[] args) {
        CourseRepository courses = new InMemoryCourseRepository();
        courses.add("Java Basics", "A. Nurlanov", "Variables, loops and classes in Java.", 15000);
        courses.add("Design Patterns", "F. Satybaldiyeva", "GoF patterns with real examples.", 25000);
        courses.add("SQL for Beginners", "D. Omarova", "Tables, SELECT and JOIN.", 12000);

        CatalogService catalog = new CatalogService(courses);
        CartService cartService = new CartService(courses);

        CheckoutService checkout = new CheckoutService();
        checkout.subscribe(new EnrollmentListener());
        checkout.subscribe(new ConsoleReceiptNotifier(System.out));

        List<PaymentProcessor> processors = List.of(new KaspiPaymentProcessor(), new CardPaymentProcessor());

        Map<String, CertificateGenerator> generators = new LinkedHashMap<>();
        generators.put("Text", new TextCertificateGenerator());
        generators.put("HTML", new HtmlCertificateGenerator());

        Student student = new Student("Student");
        new ConsoleMenu(catalog, cartService, checkout, processors, generators,
                student, new Scanner(System.in), System.out).run();
    }
}
