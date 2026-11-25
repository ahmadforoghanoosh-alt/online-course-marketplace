package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Course;
import java.util.List;

/** Command: remove a course from the cart. Undo puts it back at the same place. */
public class RemoveFromCartCommand implements CartCommand {
    private final Cart cart;
    private final long courseId;
    private Course removed;
    private int index;

    public RemoveFromCartCommand(Cart cart, long courseId) {
        this.cart = cart;
        this.courseId = courseId;
    }

    @Override
    public void execute() {
        List<Course> courses = cart.getCourses();
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).id() == courseId) {
                removed = courses.get(i);
                index = i;
            }
        }
        cart.remove(courseId); // throws if the course is not in the cart
    }

    @Override
    public void undo() {
        cart.restore(index, removed);
    }

    @Override
    public String describe() {
        return "remove " + (removed == null ? "course " + courseId : removed.title());
    }
}
