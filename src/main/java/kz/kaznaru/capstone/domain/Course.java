package kz.kaznaru.capstone.domain;

/** A course that students can buy. */
public record Course(long id, String title, String instructor, String description, double price) {
    public Course {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (!(price > 0) || Double.isInfinite(price)) {
            throw new IllegalArgumentException("price must be a number greater than 0");
        }
    }
}
