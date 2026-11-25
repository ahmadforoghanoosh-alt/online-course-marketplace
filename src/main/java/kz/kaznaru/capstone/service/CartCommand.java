package kz.kaznaru.capstone.service;

/**
 * Command pattern: one change of the cart as an object, so it can be undone later (US-05).
 */
public interface CartCommand {
    /** Does the change. If it throws, nothing changed and the command is not saved in the history. */
    void execute();

    /** Puts the cart back exactly as it was before execute(). */
    void undo();

    /** Short text for the user, for example "add Java Basics". */
    String describe();
}
