package kz.kaznaru.capstone.ui;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.Order;
import kz.kaznaru.capstone.domain.OrderStatus;
import kz.kaznaru.capstone.domain.Student;
import kz.kaznaru.capstone.service.CartService;
import kz.kaznaru.capstone.service.CatalogService;
import kz.kaznaru.capstone.service.CertificateGenerator;
import kz.kaznaru.capstone.service.CheckoutService;
import kz.kaznaru.capstone.service.PaymentProcessor;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/** Console UI. It only talks to the service layer. */
public class ConsoleMenu {
    private final CatalogService catalog;
    private final CartService cartService;
    private final CheckoutService checkout;
    private final List<PaymentProcessor> processors;
    private final Map<String, CertificateGenerator> generators;
    private final Student student;
    private final Cart cart = new Cart();
    private final Scanner in;
    private final PrintStream out;

    public ConsoleMenu(CatalogService catalog, CartService cartService, CheckoutService checkout,
                       List<PaymentProcessor> processors, Map<String, CertificateGenerator> generators,
                       Student student, Scanner in, PrintStream out) {
        this.catalog = catalog;
        this.cartService = cartService;
        this.checkout = checkout;
        this.processors = processors;
        this.generators = generators;
        this.student = student;
        this.in = in;
        this.out = out;
    }

    public void run() {
        out.println("Welcome to the Online Course Marketplace, " + student.getName() + "!");
        boolean running = true;
        while (running && in.hasNextLine()) {
            printMenu();
            String choice = in.nextLine().trim();
            try {
                running = handle(choice);
            } catch (IllegalArgumentException | IllegalStateException e) {
                out.println("Error: " + e.getMessage());
            }
        }
        out.println("Goodbye!");
    }

    private void printMenu() {
        out.println();
        out.println("1. Show all courses        5. Remove course from cart");
        out.println("2. Show course details     6. Checkout");
        out.println("3. Add new course          7. My courses");
        out.println("4. Add course to cart      8. Complete a course and get certificate");
        out.println("0. Exit");
        out.print("Choose: ");
    }

    private boolean handle(String choice) {
        switch (choice) {
            case "1" -> showCourses();
            case "2" -> showDetails();
            case "3" -> addCourse();
            case "4" -> addToCart();
            case "5" -> removeFromCart();
            case "6" -> doCheckout();
            case "7" -> showMyCourses();
            case "8" -> completeAndCertify();
            case "0" -> {
                return false;
            }
            default -> out.println("Unknown option");
        }
        return true;
    }

    private void showCourses() {
        List<Course> courses = catalog.listCourses();
        if (courses.isEmpty()) {
            out.println("No courses yet");
            return;
        }
        for (Course c : courses) {
            out.printf("%d. %s (%s) - %.2f KZT%n", c.id(), c.title(), c.instructor(), c.price());
        }
    }

    private void showDetails() {
        long id = readLong("Course id: ");
        catalog.findCourse(id).ifPresentOrElse(
                c -> out.printf("%s%nInstructor: %s%n%s%nPrice: %.2f KZT%n",
                        c.title(), c.instructor(), c.description(), c.price()),
                () -> out.println("Course not found"));
    }

    private void addCourse() {
        String title = readLine("Title: ");
        String instructor = readLine("Instructor: ");
        String description = readLine("Description: ");
        double price = readDouble("Price (KZT): ");
        Course course = catalog.addCourse(title, instructor, description, price);
        out.println("Course added with id " + course.id());
    }

    private void addToCart() {
        Course course = cartService.addToCart(cart, readLong("Course id: "));
        out.println(course.title() + " added to cart");
        showCart();
    }

    private void removeFromCart() {
        cartService.removeFromCart(cart, readLong("Course id: "));
        out.println("Removed from cart");
        showCart();
    }

    private void showCart() {
        for (Course c : cart.getCourses()) {
            out.printf("  %s  %.2f KZT%n", c.title(), c.price());
        }
        out.printf("Cart total: %.2f KZT%n", cartService.total(cart));
    }

    private void doCheckout() {
        if (cart.isEmpty()) {
            out.println("Cart is empty");
            return;
        }
        showCart();
        for (int i = 0; i < processors.size(); i++) {
            out.println((i + 1) + ". " + processors.get(i).name());
        }
        int index = (int) readLong("Payment method: ") - 1;
        if (index < 0 || index >= processors.size()) {
            throw new IllegalArgumentException("Unknown payment method");
        }
        Order order = checkout.checkout(student, cart, processors.get(index));
        if (order.getStatus() == OrderStatus.PAID) {
            out.println("Payment successful. You are enrolled!");
        } else {
            out.println("Payment failed. Your cart is not changed.");
        }
    }

    private void showMyCourses() {
        List<Course> courses = student.getEnrolled();
        if (courses.isEmpty()) {
            out.println("You have no courses yet");
            return;
        }
        for (Course c : courses) {
            String done = student.hasCompleted(c) ? " (completed)" : "";
            out.println(c.id() + ". " + c.title() + done);
        }
    }

    private void completeAndCertify() {
        long id = readLong("Course id: ");
        Course course = student.getEnrolled().stream()
                .filter(c -> c.id() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("This course is not in My courses"));
        student.complete(course);

        List<String> formats = new ArrayList<>(generators.keySet());
        for (int i = 0; i < formats.size(); i++) {
            out.println((i + 1) + ". " + formats.get(i));
        }
        int index = (int) readLong("Certificate format: ") - 1;
        if (index < 0 || index >= formats.size()) {
            throw new IllegalArgumentException("Unknown format");
        }
        out.println(generators.get(formats.get(index)).generate(student, course, LocalDate.now()));
    }

    private String readLine(String prompt) {
        out.print(prompt);
        return in.nextLine().trim();
    }

    private long readLong(String prompt) {
        String text = readLine(prompt);
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please enter a number");
        }
    }

    private double readDouble(String prompt) {
        String text = readLine(prompt);
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please enter a number");
        }
    }
}
