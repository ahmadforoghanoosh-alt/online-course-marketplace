package kz.kaznaru.capstone.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kz.kaznaru.capstone.domain.FakeCourseRepository;
import kz.kaznaru.capstone.domain.FakeOrderRepository;
import kz.kaznaru.capstone.domain.PaymentGateway;
import kz.kaznaru.capstone.domain.PercentDiscount;
import kz.kaznaru.capstone.domain.Student;
import kz.kaznaru.capstone.service.CartService;
import kz.kaznaru.capstone.service.CatalogService;
import kz.kaznaru.capstone.service.CertificateGenerator;
import kz.kaznaru.capstone.service.CheckoutService;
import kz.kaznaru.capstone.service.DiscountService;
import kz.kaznaru.capstone.service.EnrollmentListener;
import kz.kaznaru.capstone.service.LearningService;
import kz.kaznaru.capstone.service.OrderHistoryService;
import kz.kaznaru.capstone.service.PaymentProcessor;
import kz.kaznaru.capstone.service.TextCertificateGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** The API is tested without a network: we call the controller directly with method, path and query. */
class ApiControllerTest {
    private ApiController api;

    private static PaymentProcessor fake(String name, boolean result) {
        return new PaymentProcessor() {
            @Override
            protected PaymentGateway createGateway() {
                return (amount, ref) -> result;
            }

            @Override
            public String name() {
                return name;
            }
        };
    }

    @BeforeEach
    void setUp() {
        FakeCourseRepository repo = new FakeCourseRepository();
        repo.add("Java", "Teacher", "Basics", 15000);
        repo.add("SQL", "Teacher", "Tables", 12000);
        CheckoutService checkout = new CheckoutService();
        OrderHistoryService history = new OrderHistoryService(new FakeOrderRepository());
        checkout.subscribe(new EnrollmentListener());
        checkout.subscribe(history);
        Map<String, CertificateGenerator> generators = new LinkedHashMap<>();
        generators.put("Text", new TextCertificateGenerator());
        api = new ApiController(new CatalogService(repo), new CartService(repo), checkout,
                new DiscountService(Map.of("STUDENT10", new PercentDiscount(10))),
                List.of(fake("Kaspi", true), fake("Card", false)), new LearningService(generators),
                new Student("Ali"), history);
    }

    @Test
    void listAndFindCourses() {
        ApiResponse all = api.handle("GET", "/courses", null);
        assertEquals(200, all.status());
        assertTrue(all.body().contains("\"title\":\"Java\""));
        assertTrue(all.body().contains("\"price\":12000.00"));

        assertEquals(200, api.handle("GET", "/courses/2", null).status());
        assertEquals(404, api.handle("GET", "/courses/9", null).status());
        assertEquals(400, api.handle("GET", "/courses/abc", null).status());
    }

    @Test
    void addCourseWithQueryParameters() {
        ApiResponse r = api.handle("POST", "/courses", "title=Git+Basics&instructor=B.%20Aliyev&price=5000");

        assertEquals(201, r.status());
        assertTrue(r.body().contains("\"id\":3"));
        assertTrue(r.body().contains("\"title\":\"Git Basics\""));
        assertEquals(400, api.handle("POST", "/courses", "title=X").status());
    }

    @Test
    void cartDiscountUndoAndCheckout() {
        api.handle("POST", "/cart/1", null);
        api.handle("POST", "/cart/2", null);
        api.handle("DELETE", "/cart/1", null);
        ApiResponse undo = api.handle("POST", "/cart/undo", null);
        assertTrue(undo.body().contains("\"undone\":\"remove Java\""));
        assertTrue(undo.body().contains("\"total\":27000.00"));

        ApiResponse discount = api.handle("POST", "/cart/discount", "code=student10");
        assertTrue(discount.body().contains("\"total\":24300.00"));

        ApiResponse failed = api.handle("POST", "/checkout", "method=card");
        assertTrue(failed.body().contains("\"status\":\"NEW\""));
        ApiResponse paid = api.handle("POST", "/checkout", "method=Kaspi");
        assertTrue(paid.body().contains("\"status\":\"PAID\""));

        ApiResponse mine = api.handle("GET", "/my-courses", null);
        assertTrue(mine.body().contains("\"title\":\"SQL\""));
        ApiResponse cert = api.handle("POST", "/my-courses/2/certificate", "format=Text");
        assertEquals(200, cert.status());
        assertTrue(cert.body().contains("has completed the course"));
    }

    @Test
    void errorsHaveClearStatusCodes() {
        assertEquals(409, api.handle("POST", "/cart/undo", null).status());          // nothing to undo
        assertEquals(409, api.handle("POST", "/checkout", "method=Kaspi").status()); // empty cart
        assertEquals(400, api.handle("POST", "/cart/9", null).status());             // unknown course
        assertEquals(400, api.handle("POST", "/checkout", "method=Bitcoin").status());
        assertEquals(404, api.handle("GET", "/unknown", null).status());
        assertTrue(api.handle("GET", "/payment-methods", null).body().contains("\"Kaspi\""));
    }

    @Test
    void courseCrudThroughTheApi() {
        ApiResponse created = api.handle("POST", "/courses", "title=Docker&instructor=B&description=Containers&price=9000");
        assertEquals(201, created.status());

        ApiResponse updated = api.handle("PUT", "/courses/3", "title=Docker+Pro&instructor=B&description=Containers&price=11000");
        assertEquals(200, updated.status());
        assertTrue(updated.body().contains("\"title\":\"Docker Pro\""));

        assertEquals(200, api.handle("DELETE", "/courses/3", null).status());
        assertEquals(404, api.handle("GET", "/courses/3", null).status());
    }

    @Test
    void crudErrorsGiveClearStatusCodes() {
        assertEquals(404, api.handle("PUT", "/courses/99", "title=X&instructor=B&description=d&price=100").status());
        assertEquals(404, api.handle("DELETE", "/courses/99", null).status());
        assertEquals(400, api.handle("PUT", "/courses/1", "title=&instructor=B&description=d&price=100").status());
    }

    @Test
    void paidOrderAppearsInTheOrderHistory() {
        assertEquals("[]", api.handle("GET", "/orders", null).body());
        api.handle("POST", "/cart/1", null);
        api.handle("POST", "/checkout", "method=Kaspi");

        ApiResponse orders = api.handle("GET", "/orders", null);

        assertEquals(200, orders.status());
        assertTrue(orders.body().contains("\"reference\":\"ORD-1\""));
        assertTrue(orders.body().contains("\"title\":\"Java\""));
    }

    @Test
    void certificateFormatsAreListed() {
        ApiResponse formats = api.handle("GET", "/certificate-formats", null);

        assertEquals(200, formats.status());
        assertEquals("[\"Text\"]", formats.body());
    }
}
