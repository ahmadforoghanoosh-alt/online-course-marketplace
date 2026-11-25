package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.Student;

/** Check 2: the student does not pay twice for a course that is already in My courses. */
public class NotAlreadyBoughtCheck extends CheckoutCheck {
    @Override
    protected void verify(Student student, Cart cart, PaymentStrategy payment) {
        for (Course course : cart.getCourses()) {
            if (student.isEnrolled(course)) {
                throw new IllegalStateException("You already have \"" + course.title() + "\" in My courses");
            }
        }
    }
}
