package kz.kaznaru.capstone.domain;

import java.util.ArrayList;
import java.util.List;

/** The courses a student wants to buy, and an optional discount (US-07). */
public class Cart {
    private final List<Course> courses = new ArrayList<>();
    private DiscountStrategy discount;

    public void add(Course course) {
        if (contains(course.id())) {
            throw new IllegalStateException(course.title() + " is already in the cart");
        }
        courses.add(course);
    }

    public void remove(long courseId) {
        boolean removed = courses.removeIf(c -> c.id() == courseId);
        if (!removed) {
            throw new IllegalArgumentException("Course " + courseId + " is not in the cart");
        }
    }

    public boolean contains(long courseId) {
        return courses.stream().anyMatch(c -> c.id() == courseId);
    }

    public List<Course> getCourses() {
        return List.copyOf(courses);
    }

    /** Sum of the prices, before the discount. */
    public double subtotal() {
        return courses.stream().mapToDouble(Course::price).sum();
    }

    /** What the student pays: the subtotal with the discount rule applied. */
    public double total() {
        return discount == null ? subtotal() : discount.apply(subtotal());
    }

    /** Uses this discount rule from now on. A new code replaces the old one. */
    public void applyDiscount(DiscountStrategy discount) {
        if (discount == null) {
            throw new IllegalArgumentException("discount must not be null");
        }
        this.discount = discount;
    }

    public boolean hasDiscount() {
        return discount != null;
    }

    public DiscountStrategy getDiscount() {
        return discount;
    }

    public boolean isEmpty() {
        return courses.isEmpty();
    }

    public void clear() {
        courses.clear();
        discount = null;
    }
}
