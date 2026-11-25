package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.CourseRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/** Use cases for the course catalogue (US-01, US-02, US-03, US-11 edit and delete). */
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

    /** US-11: change a course. The same checks as for a new course (Builder) are used. */
    public Course updateCourse(long id, String title, String instructor, String description, double price) {
        return repository.update(id, title, instructor, description, price)
                .orElseThrow(() -> new NoSuchElementException("Course not found"));
    }

    /** US-11: remove a course from the catalogue. */
    public void deleteCourse(long id) {
        if (!repository.delete(id)) {
            throw new NoSuchElementException("Course not found");
        }
    }
}
