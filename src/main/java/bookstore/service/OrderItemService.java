package bookstore.service;

import bookstore.dto.orderitem.OrderItemDto;
import java.util.Set;

public interface OrderItemService {

    OrderItemDto getOrderItemByOrderIdAndItemId(String email, Long orderId, Long id);

    Set<OrderItemDto> getOrderItemsByOrderId(String email, Long orderId);

}
