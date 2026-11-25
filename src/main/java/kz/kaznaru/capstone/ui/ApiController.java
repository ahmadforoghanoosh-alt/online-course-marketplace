package kz.kaznaru.capstone.ui;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.Order;
import kz.kaznaru.capstone.domain.Student;
import kz.kaznaru.capstone.service.AddToCartCommand;
import kz.kaznaru.capstone.service.ApplyDiscountCommand;
import kz.kaznaru.capstone.service.CartCommand;
import kz.kaznaru.capstone.service.CartHistory;
import kz.kaznaru.capstone.service.CartService;
import kz.kaznaru.capstone.service.CatalogService;
import kz.kaznaru.capstone.service.CheckoutService;
import kz.kaznaru.capstone.service.DiscountService;
import kz.kaznaru.capstone.service.LearningService;
import kz.kaznaru.capstone.service.PaymentStrategy;
import kz.kaznaru.capstone.service.RemoveFromCartCommand;

/**
 * Second UI: a small REST API over the SAME services as ConsoleMenu (ADR-0009).
 * It knows nothing about HTTP: it gets a method, a path and a query, and returns a status and JSON,
 * so it can be tested without a network. HttpApiServer only connects it to the real HTTP server.
 */
public class ApiController {
    private final CatalogService catalog;
    private final CartService cartService;
    private final CheckoutService checkout;
    private final DiscountService discounts;
    private final List<PaymentStrategy> payments;
    private final LearningService learning;
    private final Student student;
    private final Cart cart = new Cart();
    private final CartHistory history = new CartHistory();

    public ApiController(CatalogService catalog, CartService cartService, CheckoutService checkout,
                         DiscountService discounts, List<PaymentStrategy> payments, LearningService learning,
                         Student student) {
        this.catalog = catalog;
        this.cartService = cartService;
        this.checkout = checkout;
        this.discounts = discounts;
        this.payments = payments;
        this.learning = learning;
        this.student = student;
    }

    /** Handles one request. Errors from the services become 400 (bad input), 404 (not found) or 409 (not allowed now). */
    public ApiResponse handle(String method, String path, String rawQuery) {
        try {
            return route(method.toUpperCase(), parts(path), query(rawQuery));
        } catch (NoSuchElementException e) {
            return error(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            return error(400, e.getMessage());
        } catch (IllegalStateException e) {
            return error(409, e.getMessage());
        }
    }

    private ApiResponse route(String method, List<String> p, Map<String, String> q) {
        String first = p.isEmpty() ? "" : p.get(0);
        switch (method + " " + first + "/" + p.size()) {
            case "GET courses/1":
                return ok(catalog.listCourses().stream().map(ApiController::course).toList());
            case "GET courses/2":
                return catalog.findCourse(id(p.get(1)))
                        .map(c -> ok(course(c)))
                        .orElseGet(() -> error(404, "Course not found"));
            case "POST courses/1":
                Course added = catalog.addCourse(q.get("title"), q.get("instructor"), q.get("description"),
                        number(q.get("price")));
                return new ApiResponse(201, Json.write(course(added)));
            case "PUT courses/2":
                Course changed = catalog.updateCourse(id(p.get(1)), q.get("title"), q.get("instructor"),
                        q.get("description"), number(q.get("price")));
                return ok(course(changed));
            case "DELETE courses/2":
                long removed = id(p.get(1));
                catalog.deleteCourse(removed);
                Map<String, Object> gone = new LinkedHashMap<>();
                gone.put("deleted", removed);
                return ok(gone);
            case "GET cart/1":
                return ok(cartJson());
            case "POST cart/2":
                if (p.get(1).equals("undo")) {
                    CartCommand undone = history.undo();
                    Map<String, Object> body = cartJson();
                    body.put("undone", undone.describe());
                    return ok(body);
                }
                if (p.get(1).equals("discount")) {
                    history.run(new ApplyDiscountCommand(discounts, cart, q.get("code")));
                    return ok(cartJson());
                }
                history.run(new AddToCartCommand(cartService, cart, id(p.get(1))));
                return ok(cartJson());
            case "DELETE cart/2":
                history.run(new RemoveFromCartCommand(cart, id(p.get(1))));
                return ok(cartJson());
            case "GET payment-methods/1":
                return ok(payments.stream().map(PaymentStrategy::name).toList());
            case "POST checkout/1":
                return checkout(q.get("method"));
            case "GET my-courses/1":
                return ok(learning.myCourses(student).stream().map(ApiController::course).toList());
            case "POST my-courses/3":
                if (!p.get(2).equals("certificate")) {
                    break;
                }
                String format = q.getOrDefault("format", "Text");
                Map<String, Object> cert = new LinkedHashMap<>();
                cert.put("certificate", learning.completeAndCertify(student, id(p.get(1)), format, LocalDate.now()));
                return ok(cert);
            default:
                break;
        }
        return error(404, "Unknown endpoint: " + method + " /" + String.join("/", p));
    }

    private ApiResponse checkout(String methodName) {
        PaymentStrategy payment = payments.stream()
                .filter(s -> s.name().equalsIgnoreCase(methodName == null ? "" : methodName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown payment method: " + methodName));
        Order order = checkout.checkout(student, cart, payment);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("order", order.getId());
        body.put("status", order.getStatus().name());
        body.put("total", order.getTotal());
        if (order.getStatus().name().equals("PAID")) {
            history.clear();
        }
        return ok(body);
    }

    private Map<String, Object> cartJson() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("courses", cart.getCourses().stream().map(ApiController::course).toList());
        body.put("subtotal", cart.subtotal());
        body.put("discount", cart.hasDiscount() ? cart.getDiscount().describe() : null);
        body.put("total", cart.total());
        body.put("canUndo", history.canUndo());
        return body;
    }

    private static Map<String, Object> course(Course c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", c.id());
        m.put("title", c.title());
        m.put("instructor", c.instructor());
        m.put("description", c.description());
        m.put("price", c.price());
        return m;
    }

    private static ApiResponse ok(Object body) {
        return new ApiResponse(200, Json.write(body));
    }

    private static ApiResponse error(int status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", message);
        return new ApiResponse(status, Json.write(body));
    }

    private static List<String> parts(String path) {
        return List.of(path.split("/")).stream().filter(s -> !s.isBlank()).toList();
    }

    private static Map<String, String> query(String raw) {
        Map<String, String> q = new LinkedHashMap<>();
        if (raw == null || raw.isBlank()) {
            return q;
        }
        for (String pair : raw.split("&")) {
            String[] kv = pair.split("=", 2);
            q.put(decode(kv[0]), kv.length > 1 ? decode(kv[1]) : "");
        }
        return q;
    }

    private static String decode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    private static long id(String text) {
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please use a number as id");
        }
    }

    private static double number(String text) {
        if (text == null) {
            throw new IllegalArgumentException("price is required");
        }
        try {
            return Double.parseDouble(text.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please enter a number");
        }
    }
}
