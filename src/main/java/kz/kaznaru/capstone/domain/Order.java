package kz.kaznaru.capstone.domain;

import java.util.List;

/** An order made from the cart at checkout. */
public class Order {
    private final String id;
    private final Student student;
    private final List<Course> courses;
    private final double total;
    private OrderStatus status = OrderStatus.NEW;

    public Order(String id, Student student, List<Course> courses) {
        if (courses.isEmpty()) {
            throw new IllegalArgumentException("an order needs at least one course");
        }
        this.id = id;
        this.student = student;
        this.courses = List.copyOf(courses);
        this.total = courses.stream().mapToDouble(Course::price).sum();
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
