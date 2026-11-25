package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.CourseRepository;

/** Use case for the cart that needs the catalogue: add a course by id (US-04). */
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
}
