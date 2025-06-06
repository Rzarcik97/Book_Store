package bookstore.repository.orderitem;

import bookstore.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    @Query("SELECT oi FROM OrderItem oi "
            + "WHERE oi.id = :itemId AND oi.order.user.email = :email AND oi.order.id = :orderId")
    OrderItem findByUserEmailAndOrderId(@Param("email") String email, Long orderId, Long itemId);
}
