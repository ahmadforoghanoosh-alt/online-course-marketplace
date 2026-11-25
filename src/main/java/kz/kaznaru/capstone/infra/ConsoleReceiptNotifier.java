package kz.kaznaru.capstone.infra;

import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.Order;
import kz.kaznaru.capstone.domain.OrderListener;
import java.io.PrintStream;
import java.util.Locale;

/** Observer: prints the receipt when an order is paid. */
public class ConsoleReceiptNotifier implements OrderListener {
    private final PrintStream out;

    public ConsoleReceiptNotifier(PrintStream out) {
        this.out = out;
    }

    @Override
    public void onOrderPaid(Order order) {
        out.println("----- Receipt -----");
        out.println("Order: " + order.getId());
        out.println("Student: " + order.getStudent().getName());
        for (Course course : order.getCourses()) {
            out.printf(Locale.ROOT, "  %s  %.2f KZT%n", course.title(), course.price());
        }
        out.printf(Locale.ROOT, "Total: %.2f KZT%n", order.getTotal());
        out.println("-------------------");
    }
}
