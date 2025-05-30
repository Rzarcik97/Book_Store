package bookstore.controller;

import bookstore.dto.cartitem.CartItemRequestDto;
import bookstore.dto.shoppingcart.ShoppingCartDto;
import bookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ShoppingCart_controller", description = "ShoppingCart management application")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    @Operation(summary = "Get all CartItem in shopping cart",
            description = "Get all CartItem from Database related to your shopping cart")
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        String email = authentication.getName();
        return shoppingCartService.getShoppingCart(email);
    }

    @PostMapping
    @Operation(summary = "add CartItem in shopping cart",
            description = "add cartItem to Database related to your shopping cart,"
                    + " if cartItem already exist,the quantity of books in cartItem"
                    + " will be updated instead")
    public ShoppingCartDto addBooksToShoppingCart(
            Authentication authentication,
            @RequestBody CartItemRequestDto cartItemRequestDto) {
        String email = authentication.getName();
        return shoppingCartService.addBooksToShoppingCart(email, cartItemRequestDto);
    }

    @PutMapping("/cart-items/{id}")
    @Operation(summary = "Update books quantity in shopping cart",
            description = "Update quantity of cartItem from Database related to your shopping cart")
    public ShoppingCartDto updateShoppingCart(Authentication authentication,
                                              @PathVariable Long id,
                                              @RequestBody Map<String,Integer> update) {
        String email = authentication.getName();
        int quantity = update.get("quantity");
        return shoppingCartService.updateShoppingCart(email, id, quantity);
    }

    @DeleteMapping("/cart-items/{id}")
    @Operation(summary = "Delete Book from shopping cart",
            description = "Delete cartItem from Database related to your shopping cart")
    public void removeBooksFromShoppingCart(Authentication authentication,
            @PathVariable Long id) {
        String email = authentication.getName();
        shoppingCartService.removeBooksFromShoppingCart(email,id);
    }
}
