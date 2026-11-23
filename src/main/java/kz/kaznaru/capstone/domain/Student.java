package kz.kaznaru.capstone.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** A student with the courses he bought and the courses he finished. */
public class Student {
    private final String name;
    private final List<Course> enrolled = new ArrayList<>();
    private final Set<Long> completed = new HashSet<>();

    public Student(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void enroll(Course course) {
        if (!isEnrolled(course)) {
            enrolled.add(course);
        }
    }

    public boolean isEnrolled(Course course) {
        return enrolled.stream().anyMatch(c -> c.id() == course.id());
    }

    public List<Course> getEnrolled() {
        return List.copyOf(enrolled);
    }

    public void complete(Course course) {
        if (!isEnrolled(course)) {
            throw new IllegalStateException("You are not enrolled in " + course.title());
        }
        completed.add(course.id());
    }

    public boolean hasCompleted(Course course) {
        return completed.contains(course.id());
    }
}
