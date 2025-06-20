package bookstore.repository.order;

import bookstore.model.Order;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT o FROM Order o "
            + "LEFT JOIN FETCH o.user u "
            + "LEFT JOIN FETCH o.orderItems oi "
            + "LEFT JOIN FETCH oi.book b WHERE u.email = :email")
    List<Order> getOrderByUser(@NotNull @Param("email") String email);

    Order getOrderById(Long id);

    @Query("SELECT o FROM Order o "
            + "LEFT JOIN FETCH o.user u "
            + "LEFT JOIN FETCH o.orderItems oi "
            + "LEFT JOIN FETCH oi.book b "
            + "WHERE u.email = :email AND o.id = :id")
    Order getOrderByOrderIdAndUser_Email(Long id, String email);
}

