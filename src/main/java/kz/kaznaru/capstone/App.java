package kz.kaznaru.capstone;

import kz.kaznaru.capstone.domain.CourseRepository;
import kz.kaznaru.capstone.infra.InMemoryCourseRepository;

/** Composition root: the ONLY place allowed to wire concrete classes together. */
public final class App {
    private App() { }

    public static void main(String[] args) {
        CourseRepository courses = new InMemoryCourseRepository();
        courses.add("Java Basics", "A. Nurlanov", "Variables, loops and classes in Java.", 15000);
        courses.add("Design Patterns", "F. Satybaldiyeva", "GoF patterns with real examples.", 25000);
        courses.add("SQL for Beginners", "D. Omarova", "Tables, SELECT and JOIN.", 12000);
        courses.findAll().forEach(c -> System.out.println(c.id() + ": " + c.title()));
    }
}
