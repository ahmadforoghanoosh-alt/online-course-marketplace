package kz.kaznaru.capstone.service;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Command pattern, the invoker: runs cart commands and keeps the done ones on a stack,
 * so the last change can be undone (US-05).
 */
public class CartHistory {
    private final Deque<CartCommand> done = new ArrayDeque<>();

    /** Runs the command. Only a command that worked is saved. */
    public void run(CartCommand command) {
        command.execute();
        done.push(command);
    }

    /** Undoes the last change and returns it. */
    public CartCommand undo() {
        if (done.isEmpty()) {
            throw new IllegalStateException("Nothing to undo");
        }
        CartCommand last = done.pop();
        last.undo();
        return last;
    }

    public boolean canUndo() {
        return !done.isEmpty();
    }

    /** After a paid order the cart is empty, so the old changes can not be undone any more. */
    public void clear() {
        done.clear();
    }
}
