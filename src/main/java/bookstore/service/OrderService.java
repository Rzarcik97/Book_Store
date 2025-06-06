package bookstore.service;

import bookstore.dto.order.OrderDto;
import bookstore.dto.order.StatusDto;
import bookstore.model.Order;
import java.util.List;

public interface OrderService {

    List<OrderDto> getOrderHistory(String email);

    OrderDto createOrder(String email, String shippingAddress);

    StatusDto updateOrderStatus(Long orderId, Order.Status status);
}
