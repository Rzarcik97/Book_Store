package bookstore.service;

import bookstore.dto.cartitem.CartItemRequestDto;
import bookstore.dto.shoppingcart.ShoppingCartDto;

public interface ShoppingCartService {

    ShoppingCartDto getShoppingCart(String email);

    ShoppingCartDto addBooksToShoppingCart(String email, CartItemRequestDto cartItemRequestDto);

    ShoppingCartDto updateShoppingCart(String email, Long id,int quantity);

    void removeBooksFromShoppingCart(String email, Long cartItemId);
}
