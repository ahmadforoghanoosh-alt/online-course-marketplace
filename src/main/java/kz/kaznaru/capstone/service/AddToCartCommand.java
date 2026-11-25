package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Cart;
import kz.kaznaru.capstone.domain.Course;

/** Command: add a course to the cart by id. Undo removes it again. */
public class AddToCartCommand implements CartCommand {
    private final CartService cartService;
    private final Cart cart;
    private final long courseId;
    private Course added;

    public AddToCartCommand(CartService cartService, Cart cart, long courseId) {
        this.cartService = cartService;
        this.cart = cart;
        this.courseId = courseId;
    }

    @Override
    public void execute() {
        added = cartService.addToCart(cart, courseId);
    }

    @Override
    public void undo() {
        cart.remove(added.id());
    }

    @Override
    public String describe() {
        return "add " + (added == null ? "course " + courseId : added.title());
    }

    public Course getAdded() {
        return added;
    }
}
