package kz.kaznaru.capstone.domain;

import java.util.List;
import java.util.Optional;

/** Port owned by the domain; implemented in infra (Repository pattern, Lecture 14). */
public interface CourseRepository {
    Course add(String title, String instructor, String description, double price);
    Optional<Course> findById(long id);
    List<Course> findAll();
}
