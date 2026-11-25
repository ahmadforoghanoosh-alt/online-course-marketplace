package kz.kaznaru.capstone;

import kz.kaznaru.capstone.domain.CourseRepository;
import kz.kaznaru.capstone.domain.DiscountStrategy;
import kz.kaznaru.capstone.domain.FixedAmountDiscount;
import kz.kaznaru.capstone.domain.PercentDiscount;
import kz.kaznaru.capstone.domain.Student;
import kz.kaznaru.capstone.infra.CardPaymentProcessor;
import kz.kaznaru.capstone.infra.ConsoleReceiptNotifier;
import kz.kaznaru.capstone.infra.InMemoryCourseRepository;
import kz.kaznaru.capstone.infra.KaspiPaymentProcessor;
import kz.kaznaru.capstone.infra.SqliteCourseRepository;
import kz.kaznaru.capstone.service.CartService;
import kz.kaznaru.capstone.service.CatalogService;
import kz.kaznaru.capstone.service.CertificateGenerator;
import kz.kaznaru.capstone.service.CheckoutService;
import kz.kaznaru.capstone.service.DiscountService;
import kz.kaznaru.capstone.service.EnrollmentListener;
import kz.kaznaru.capstone.service.HtmlCertificateGenerator;
import kz.kaznaru.capstone.service.LearningService;
import kz.kaznaru.capstone.service.PaymentStrategy;
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
        CourseRepository courses = chooseRepository(args);
        if (courses.findAll().isEmpty()) {
            courses.add("Java Basics", "A. Nurlanov", "Variables, loops and classes in Java.", 15000);
            courses.add("Design Patterns", "F. Satybaldiyeva", "GoF patterns with real examples.", 25000);
            courses.add("SQL for Beginners", "D. Omarova", "Tables, SELECT and JOIN.", 12000);
        }

        CatalogService catalog = new CatalogService(courses);
        CartService cartService = new CartService(courses);

        CheckoutService checkout = new CheckoutService();
        checkout.subscribe(new EnrollmentListener());
        checkout.subscribe(new ConsoleReceiptNotifier(System.out));

        Map<String, DiscountStrategy> codes = new LinkedHashMap<>();
        codes.put("STUDENT10", new PercentDiscount(10));
        codes.put("WELCOME2000", new FixedAmountDiscount(2000));
        DiscountService discounts = new DiscountService(codes);

        List<PaymentStrategy> payments = List.of(new KaspiPaymentProcessor(), new CardPaymentProcessor());

        Map<String, CertificateGenerator> generators = new LinkedHashMap<>();
        generators.put("Text", new TextCertificateGenerator());
        generators.put("HTML", new HtmlCertificateGenerator());
        LearningService learning = new LearningService(generators);

        Student student = new Student("Student");
        new ConsoleMenu(catalog, cartService, checkout, discounts, payments, learning,
                student, new Scanner(System.in), System.out).run();
    }

    /** The only place that decides where courses are stored: "--memory" or the SQLite file (default). */
    static CourseRepository chooseRepository(String[] args) {
        if (List.of(args).contains("--memory")) {
            System.out.println("Storage: in memory (data is lost when the app stops)");
            return new InMemoryCourseRepository();
        }
        System.out.println("Storage: SQLite database file marketplace.db");
        return new SqliteCourseRepository("jdbc:sqlite:marketplace.db");
    }
}
