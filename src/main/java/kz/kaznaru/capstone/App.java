package kz.kaznaru.capstone;

import kz.kaznaru.capstone.domain.CourseRepository;
import kz.kaznaru.capstone.domain.DiscountStrategy;
import kz.kaznaru.capstone.domain.OrderRepository;
import kz.kaznaru.capstone.domain.FixedAmountDiscount;
import kz.kaznaru.capstone.domain.PercentDiscount;
import kz.kaznaru.capstone.domain.Student;
import kz.kaznaru.capstone.infra.CardPaymentProcessor;
import kz.kaznaru.capstone.infra.ConsoleReceiptNotifier;
import kz.kaznaru.capstone.infra.InMemoryCourseRepository;
import kz.kaznaru.capstone.infra.InMemoryOrderRepository;
import kz.kaznaru.capstone.infra.KaspiPaymentProcessor;
import kz.kaznaru.capstone.infra.SqliteCourseRepository;
import kz.kaznaru.capstone.infra.SqliteOrderRepository;
import kz.kaznaru.capstone.service.CartService;
import kz.kaznaru.capstone.service.CatalogService;
import kz.kaznaru.capstone.service.CertificateGenerator;
import kz.kaznaru.capstone.service.CheckoutService;
import kz.kaznaru.capstone.service.DiscountService;
import kz.kaznaru.capstone.service.EnrollmentListener;
import kz.kaznaru.capstone.service.HtmlCertificateGenerator;
import kz.kaznaru.capstone.service.LearningService;
import kz.kaznaru.capstone.service.OrderHistoryService;
import kz.kaznaru.capstone.service.PaymentStrategy;
import kz.kaznaru.capstone.service.TextCertificateGenerator;
import kz.kaznaru.capstone.ui.ApiController;
import kz.kaznaru.capstone.ui.ConsoleMenu;
import kz.kaznaru.capstone.ui.HttpApiServer;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/** Composition root: the ONLY place allowed to wire concrete classes together. */
public final class App {
    static final int API_PORT = 8080;

    private App() { }

    public static void main(String[] args) throws IOException {
        CourseRepository courses = chooseRepository(args);
        if (courses.findAll().isEmpty()) {
            courses.add("Java Basics", "A. Nurlanov", "Variables, loops and classes in Java.", 15000);
            courses.add("Design Patterns", "F. Satybaldiyeva", "GoF patterns with real examples.", 25000);
            courses.add("SQL for Beginners", "D. Omarova", "Tables, SELECT and JOIN.", 12000);
        }

        CatalogService catalog = new CatalogService(courses);
        CartService cartService = new CartService(courses);

        OrderHistoryService orderHistory = new OrderHistoryService(chooseOrderRepository(args));
        CheckoutService checkout = new CheckoutService(orderHistory.nextOrderNumber());
        checkout.subscribe(new EnrollmentListener());
        checkout.subscribe(new ConsoleReceiptNotifier(System.out));
        checkout.subscribe(orderHistory);   // Observer: paid orders are saved in the database

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
        orderHistory.restoreEnrollments(student);   // "My courses" comes back after a restart
        if (List.of(args).contains("--api")) {
            // Website + REST API (ADR-0009, ADR-0011): the same services, used through HTTP instead of the console.
            new HttpApiServer(new ApiController(catalog, cartService, checkout, discounts, payments, learning,
                    student, orderHistory)).start(API_PORT);
            System.out.println("Website: http://localhost:" + API_PORT + "/   API: http://localhost:" + API_PORT
                    + "/courses  (stop with Ctrl+C)");
            return;
        }
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

    /** Orders are saved in the same place as the courses: in memory with "--memory", otherwise in SQLite. */
    static OrderRepository chooseOrderRepository(String[] args) {
        if (List.of(args).contains("--memory")) {
            return new InMemoryOrderRepository();
        }
        return new SqliteOrderRepository("jdbc:sqlite:marketplace.db");
    }
}
