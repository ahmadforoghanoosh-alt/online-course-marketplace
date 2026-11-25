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
        Course course = Course.builder()
                .id(next)
                .title(title)
                .instructor(instructor)
                .description(description)
                .price(price)
                .build();
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

    @Override
    public Optional<Course> update(long id, String title, String instructor, String description, double price) {
        if (!store.containsKey(id)) {
            return Optional.empty();
        }
        Course changed = Course.builder()
                .id(id)
                .title(title)
                .instructor(instructor)
                .description(description)
                .price(price)
                .build();
        store.put(id, changed);
        return Optional.of(changed);
    }

    @Override
    public boolean delete(long id) {
        return store.remove(id) != null;
    }
}
