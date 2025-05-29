package bookstore.service.impl;

import bookstore.dto.cartitem.CartItemRequestDto;
import bookstore.dto.shoppingcart.ShoppingCartDto;
import bookstore.exceptions.EntityNotFoundException;
import bookstore.mapper.ShoppingCartMapper;
import bookstore.model.Book;
import bookstore.model.CartItem;
import bookstore.model.ShoppingCart;
import bookstore.model.User;
import bookstore.repository.book.BookRepository;
import bookstore.repository.cartitem.CartItemRepository;
import bookstore.repository.shoppingcart.ShoppingCartRepository;
import bookstore.repository.user.UserRepository;
import bookstore.service.CartItemService;
import bookstore.service.ShoppingCartService;
import java.util.HashSet;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemService cartItemService;

    @Override
    public ShoppingCartDto getShoppingCart(String email) {
        ShoppingCart shoppingCartsByUser = shoppingCartRepository.getShoppingCartsByUser(email);
        return shoppingCartMapper.toDto(shoppingCartsByUser);
    }

    @Override
    public ShoppingCartDto addBooksToShoppingCart(
            String email, CartItemRequestDto cartItemRequestDto) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("cannot find user with email: " + email));
        ShoppingCart shoppingCart = shoppingCartRepository.getShoppingCartsByUser(email);
        if (shoppingCart == null) {
            shoppingCart = new ShoppingCart();
            shoppingCart.setUser(user);
            shoppingCart.setCartItems(new HashSet<>());
        }
        Optional<CartItem> existingItem = shoppingCart.getCartItems().stream()
                .filter(ci -> ci.getBook().getId().equals(cartItemRequestDto.bookId()))
                .findFirst();

        if (existingItem.isPresent()) {
            int newQuantity = existingItem.get()
                    .getQuantity() + cartItemRequestDto.quantity();
            return updateShoppingCart(email, existingItem.get().getId(), newQuantity);
        }
        Book book = bookRepository.findById(cartItemRequestDto.bookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "cannot find Book with id:" + cartItemRequestDto.bookId()));
        cartItemService.save(book,cartItemRequestDto.quantity(),shoppingCart);
        ShoppingCart savedShoppingCart = shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toDto(savedShoppingCart);
    }

    @Override
    public ShoppingCartDto updateShoppingCart(String email, Long cartItemId, int quantity) {
        ShoppingCart shoppingCart = shoppingCartRepository.getShoppingCartsByUser(email);
        shoppingCart.getCartItems()
                .stream()
                .filter(c -> c.getId().equals(cartItemId))
                .forEach(s -> s.setQuantity(quantity));
        ShoppingCart savedShoppingCart = shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toDto(savedShoppingCart);
    }

    @Override
    public void removeBooksFromShoppingCart(String email, Long cartItemId) {
        CartItem deletedcartItem = cartItemRepository
                .findByIdAndUserEmail(email, cartItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "cannot find Cart item with id:" + cartItemId
                                + "in Your Shopping cart"));
        cartItemRepository.delete(deletedcartItem);
    }
}
