package kz.kaznaru.capstone.domain;

import java.util.List;

/** An order made from the cart at checkout. */
public class Order {
    private final String id;
    private final Student student;
    private final List<Course> courses;
    private final double total;
    private OrderStatus status = OrderStatus.NEW;

    /** Order without a discount: the total is the sum of the prices. */
    public Order(String id, Student student, List<Course> courses) {
        this(id, student, courses, courses.stream().mapToDouble(Course::price).sum());
    }

    /** Order with the total already calculated by the cart (for example after a discount). */
    public Order(String id, Student student, List<Course> courses, double total) {
        if (courses.isEmpty()) {
            throw new IllegalArgumentException("an order needs at least one course");
        }
        if (total < 0 || Double.isNaN(total)) {
            throw new IllegalArgumentException("total must not be negative");
        }
        this.id = id;
        this.student = student;
        this.courses = List.copyOf(courses);
        this.total = total;
    }

    public void markPaid() {
        status = OrderStatus.PAID;
    }

    public String getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public double getTotal() {
        return total;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
