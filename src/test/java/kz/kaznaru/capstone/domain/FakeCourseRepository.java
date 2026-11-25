package kz.kaznaru.capstone.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Simple repository for tests in service and ui (they may not use infra). */
public class FakeCourseRepository implements CourseRepository {
    private final List<Course> courses = new ArrayList<>();
    private long next = 1;

    @Override
    public Course add(String title, String instructor, String description, double price) {
        Course course = Course.builder()
                .id(next++)
                .title(title)
                .instructor(instructor)
                .description(description)
                .price(price)
                .build();
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

    @Override
    public Optional<Course> update(long id, String title, String instructor, String description, double price) {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).id() == id) {
                Course changed = Course.builder().id(id).title(title).instructor(instructor)
                        .description(description).price(price).build();
                courses.set(i, changed);
                return Optional.of(changed);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(long id) {
        return courses.removeIf(c -> c.id() == id);
    }
}
