package kz.kaznaru.capstone.infra;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.Order;
import kz.kaznaru.capstone.domain.Student;
import org.junit.jupiter.api.Test;

class ConsoleReceiptNotifierTest {
    @Test
    void printsReceipt() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ConsoleReceiptNotifier notifier = new ConsoleReceiptNotifier(new PrintStream(buffer));
        Order order = new Order("ORD-5", new Student("Ali"), List.of(new Course(1, "Java", "A", "d", 15000)));

        notifier.onOrderPaid(order);

        String text = buffer.toString();
        assertTrue(text.contains("ORD-5"));
        assertTrue(text.contains("Ali"));
        assertTrue(text.contains("Java"));
    }
}
