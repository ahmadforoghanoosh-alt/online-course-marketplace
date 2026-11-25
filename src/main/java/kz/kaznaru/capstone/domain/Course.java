package kz.kaznaru.capstone.domain;

/** A course that students can buy. Build it with Course.builder() (Builder pattern, ADR-0008). */
public record Course(long id, String title, String instructor, String description, double price) {
    public Course {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (!(price > 0) || Double.isInfinite(price)) {
            throw new IllegalArgumentException("price must be a number greater than 0");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder: every value has a name, so "title" and "instructor" (both Strings) can not be swapped by mistake,
     * and the optional parts have safe defaults.
     */
    public static final class Builder {
        private long id;
        private String title;
        private String instructor = "Unknown instructor";
        private String description = "";
        private Double price;

        private Builder() {
        }

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder instructor(String instructor) {
            if (instructor != null && !instructor.isBlank()) {
                this.instructor = instructor;
            }
            return this;
        }

        public Builder description(String description) {
            this.description = description == null ? "" : description;
            return this;
        }

        public Builder price(double price) {
            this.price = price;
            return this;
        }

        /** Checks the required parts and creates the course. */
        public Course build() {
            if (price == null) {
                throw new IllegalArgumentException("price is required");
            }
            return new Course(id, title, instructor, description, price);
        }
    }
}
