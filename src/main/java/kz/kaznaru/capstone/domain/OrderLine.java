package kz.kaznaru.capstone.domain;

/**
 * One course inside a saved order. It keeps a copy of the title, instructor and price from the day of the purchase,
 * so the order history stays correct even if the course is changed or deleted later.
 */
public record OrderLine(long courseId, String title, String instructor, double price) {

    public static OrderLine of(Course course) {
        return new OrderLine(course.id(), course.title(), course.instructor(), course.price());
    }

    /** The course as it was bought (used to give back "My courses" after a restart). */
    public Course toCourse() {
        return Course.builder().id(courseId).title(title).instructor(instructor).price(price).build();
    }
}
