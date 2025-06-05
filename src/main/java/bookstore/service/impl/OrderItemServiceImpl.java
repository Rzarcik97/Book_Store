package bookstore.service.impl;

import bookstore.dto.orderitem.OrderItemDto;
import bookstore.exceptions.EntityNotFoundException;
import bookstore.mapper.OrderItemMapper;
import bookstore.model.Order;
import bookstore.model.OrderItem;
import bookstore.repository.order.OrderRepository;
import bookstore.repository.orderitem.OrderItemRepository;
import bookstore.service.OrderItemService;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderRepository orderRepository;

    @Override
    public OrderItemDto getOrderItemByOrderIdAndItemId(String email, Long orderId, Long itemId) {
        OrderItem orderItems = orderItemRepository.findByUserEmailAndOrderId(
                email, orderId, itemId);
        if (orderItems == null) {
            throw new EntityNotFoundException("Order item with id: " + itemId
                    + " not found in Order with id: " + orderId);
        }
        return orderItemMapper.toDto(orderItems);
    }

    @Override
    public Set<OrderItemDto> getOrderItemsByOrderId(String email, Long orderId) {
        Order order = orderRepository.getOrderByOrderIdAndUser_Email(orderId, email);
        if (order == null) {
            throw new EntityNotFoundException("Cannot find order with id: "
                    + orderId + " for email: " + email);
        }
        Set<OrderItem> orderItems = order.getOrderItems();
        return orderItems.stream().map(orderItemMapper::toDto).collect(Collectors.toSet());
    }
}
