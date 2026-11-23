package kz.kaznaru.capstone.domain;

import java.util.ArrayList;
import java.util.List;

/** The courses a student wants to buy. */
public class Cart {
    private final List<Course> courses = new ArrayList<>();

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

    public double total() {
        return courses.stream().mapToDouble(Course::price).sum();
    }

    public boolean isEmpty() {
        return courses.isEmpty();
    }

    public void clear() {
        courses.clear();
    }
}
