package bookstore.service;

import bookstore.model.Book;
import bookstore.model.ShoppingCart;

public interface CartItemService {
    void save(Book book, int quantity, ShoppingCart shoppingCart);
}
