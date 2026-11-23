package kz.kaznaru.capstone.infra;

import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.CourseRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryCourseRepository implements CourseRepository {
    private final Map<Long, Course> store = new LinkedHashMap<>();
    private long next = 1;

    @Override
    public Course add(String title, String instructor, String description, double price) {
        Course course = new Course(next, title, instructor, description, price);
        store.put(course.id(), course);
        next++;
        return course;
    }

    @Override
    public Optional<Course> findById(long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Course> findAll() {
        return new ArrayList<>(store.values());
    }
}
