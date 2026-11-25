package kz.kaznaru.capstone.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import kz.kaznaru.capstone.domain.FakeCourseRepository;
import kz.kaznaru.capstone.domain.PercentDiscount;
import kz.kaznaru.capstone.domain.PaymentGateway;
import kz.kaznaru.capstone.domain.Student;
import kz.kaznaru.capstone.service.CartService;
import kz.kaznaru.capstone.service.CatalogService;
import kz.kaznaru.capstone.service.CertificateGenerator;
import kz.kaznaru.capstone.service.CheckoutService;
import kz.kaznaru.capstone.service.DiscountService;
import kz.kaznaru.capstone.service.EnrollmentListener;
import kz.kaznaru.capstone.service.LearningService;
import kz.kaznaru.capstone.service.PaymentProcessor;
import kz.kaznaru.capstone.service.TextCertificateGenerator;
import org.junit.jupiter.api.Test;

/** Runs the menu with typed input. No database, no network. */
class ConsoleMenuTest {

    private static PaymentProcessor fakeProcessor(boolean result) {
        return new PaymentProcessor() {
            @Override
            protected PaymentGateway createGateway() {
                return (amount, ref) -> result;
            }

            @Override
            public String name() {
                return result ? "Always OK" : "Always fails";
            }
        };
    }

    private String run(String input) {
        FakeCourseRepository repo = new FakeCourseRepository();
        CheckoutService checkout = new CheckoutService();
        checkout.subscribe(new EnrollmentListener());
        Map<String, CertificateGenerator> generators = new LinkedHashMap<>();
        generators.put("Text", new TextCertificateGenerator());
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        new ConsoleMenu(new CatalogService(repo), new CartService(repo), checkout,
                new DiscountService(Map.of("STUDENT10", new PercentDiscount(10))),
                List.of(fakeProcessor(true), fakeProcessor(false)), new LearningService(generators),
                new Student("Ali"), new Scanner(input), new PrintStream(buffer)).run();
        return buffer.toString();
    }

    @Test
    void mainUseCaseWorksEndToEnd() {
        String out = run(String.join("\n",
                "1",                                    // empty catalogue
                "3", "Java", "Teacher", "Basics", "15000", // add course
                "1", "2", "1",                          // list, details
                "4", "1",                               // add to cart
                "6", "2",                               // checkout fails
                "6", "1",                               // checkout ok
                "7",                                    // my courses
                "8", "1", "1",                          // complete + text certificate
                "0") + "\n");

        assertTrue(out.contains("No courses yet"));
        assertTrue(out.contains("Course added with id 1"));
        assertTrue(out.contains("Cart total: 15000.00 KZT"));
        assertTrue(out.contains("Payment failed. Your cart is not changed."));
        assertTrue(out.contains("Payment successful. You are enrolled!"));
        assertTrue(out.contains("1. Java"));
        assertTrue(out.contains("has completed the course \"Java\""));
        assertTrue(out.contains(LocalDate.now().toString()));
        assertTrue(out.contains("Goodbye!"));
    }

    @Test
    void wrongInputShowsErrors() {
        String out = run(String.join("\n",
                "99",         // unknown option
                "2", "abc",   // not a number
                "2", "5",     // unknown course
                "4", "5",     // add unknown course
                "5", "1",     // remove not in cart
                "6",          // empty cart
                "7",          // no courses
                "8", "1",     // not my course
                "0") + "\n");

        assertTrue(out.contains("Unknown option"));
        assertTrue(out.contains("Please enter a number"));
        assertTrue(out.contains("Course not found"));
        assertTrue(out.contains("Cart is empty"));
        assertTrue(out.contains("You have no courses yet"));
        assertTrue(out.contains("This course is not in My courses"));
    }

    @Test
    void discountCodeChangesCartTotal() {
        String out = run(String.join("\n",
                "3", "Java", "Teacher", "Basics", "15000",
                "9", "STUDENT10",                       // empty cart
                "4", "1",
                "9", "FREE",                            // unknown code
                "9", "student10",                       // valid code
                "6", "1",                               // pay the discounted total
                "0") + "\n");

        assertTrue(out.contains("Error: Cart is empty"));
        assertTrue(out.contains("Error: Unknown discount code: FREE"));
        assertTrue(out.contains("Discount applied: 10% off"));
        assertTrue(out.contains("Payment successful. You are enrolled!"));
    }

    @Test
    void pricesUseDotOnRussianOrKazakhComputers() {
        Locale saved = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("ru-RU"));
            String out = run(String.join("\n",
                    "3", "Java", "Teacher", "Basics", "15000,50", // comma is accepted too
                    "4", "1",
                    "0") + "\n");
            assertTrue(out.contains("Cart total: 15000.50 KZT"));
        } finally {
            Locale.setDefault(saved);
        }
    }
}
