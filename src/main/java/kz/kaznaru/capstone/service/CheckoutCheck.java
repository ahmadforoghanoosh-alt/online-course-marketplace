package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Student;

/**
 * Chain of Responsibility (ADR-0012): one small rule that an order must pass before payment.
 * Each check either stops the order with a clear message (IllegalStateException) or passes it to the next check.
 * The chain is built in App, so CheckoutService does not know which checks exist.
 */
public abstract class CheckoutCheck {
    private CheckoutCheck next;

    /** Links the next check and returns it, so a chain can be written as a.setNext(b).setNext(c). */
    public CheckoutCheck setNext(CheckoutCheck next) {
        this.next = next;
        return next;
    }

    /** Runs this check and then the rest of the chain. Throws IllegalStateException at the first failed rule. */
    public final void check(Student student, Cart cart, PaymentStrategy payment) {
        verify(student, cart, payment);
        if (next != null) {
            next.check(student, cart, payment);
        }
    }

    /** The one rule of this check. Throw IllegalStateException with a message the student can understand. */
    protected abstract void verify(Student student, Cart cart, PaymentStrategy payment);
}
