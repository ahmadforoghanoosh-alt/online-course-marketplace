package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.Order;
import kz.kaznaru.capstone.domain.OrderListener;

/** Observer: enrolls the student in every paid course (US-09). */
public class EnrollmentListener implements OrderListener {
    @Override
    public void onOrderPaid(Order order) {
        for (Course course : order.getCourses()) {
            order.getStudent().enroll(course);
        }
    }
}
