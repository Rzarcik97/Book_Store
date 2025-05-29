package bookstore.repository.shoppingcart;

import bookstore.model.ShoppingCart;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Query("SELECT s FROM ShoppingCart s "
            + "LEFT JOIN FETCH s.user u "
            + "LEFT JOIN FETCH s.cartItems ci "
            + "LEFT JOIN FETCH ci.book b WHERE u.email = :email")
    ShoppingCart getShoppingCartsByUser(@NotNull String email);
}
