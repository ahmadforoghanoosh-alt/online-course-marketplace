package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.CourseRepository;
import java.util.List;
import java.util.Optional;

/** Use cases for the course catalogue (US-01, US-02, US-03). */
public class CatalogService {
    private final CourseRepository repository;

    public CatalogService(CourseRepository repository) {
        this.repository = repository;
    }

    public Course addCourse(String title, String instructor, String description, double price) {
        return repository.add(title, instructor, description, price);
    }

    public List<Course> listCourses() {
        return repository.findAll();
    }

    public Optional<Course> findCourse(long id) {
        return repository.findById(id);
    }
}
