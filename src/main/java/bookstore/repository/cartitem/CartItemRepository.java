package bookstore.repository.cartitem;

import bookstore.model.CartItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT ci FROM CartItem ci "
            + "WHERE ci.id = :itemId AND ci.shoppingCart.user.email = :email")
    Optional<CartItem> findByIdAndUserEmail(@Param("email") String email, Long itemId);
}
