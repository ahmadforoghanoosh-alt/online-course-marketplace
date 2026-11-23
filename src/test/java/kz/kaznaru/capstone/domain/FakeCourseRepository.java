package kz.kaznaru.capstone.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Simple repository for tests in service and ui (they may not use infra). */
public class FakeCourseRepository implements CourseRepository {
    private final List<Course> courses = new ArrayList<>();

    @Override
    public Course add(String title, String instructor, String description, double price) {
        Course course = new Course(courses.size() + 1, title, instructor, description, price);
        courses.add(course);
        return course;
    }

    @Override
    public Optional<Course> findById(long id) {
        return courses.stream().filter(c -> c.id() == id).findFirst();
    }

    @Override
    public List<Course> findAll() {
        return List.copyOf(courses);
    }
}
