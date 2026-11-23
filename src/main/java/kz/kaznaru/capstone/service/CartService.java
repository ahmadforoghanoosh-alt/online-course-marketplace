package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.CourseRepository;

/** Use cases for the cart (US-04, US-05 remove, US-06). */
public class CartService {
    private final CourseRepository repository;

    public CartService(CourseRepository repository) {
        this.repository = repository;
    }

    public Course addToCart(Cart cart, long courseId) {
        Course course = repository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        cart.add(course);
        return course;
    }

    public void removeFromCart(Cart cart, long courseId) {
        cart.remove(courseId);
    }

    public double total(Cart cart) {
        return cart.total();
    }
}
