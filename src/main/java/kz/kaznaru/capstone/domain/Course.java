package kz.kaznaru.capstone.domain;

/** A course that students can buy. */
public record Course(long id, String title, String instructor, String description, double price) {
    public Course {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("price must be greater than 0");
        }
    }
}
