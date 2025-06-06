package bookstore.service.impl;

import bookstore.dto.order.OrderDto;
import bookstore.dto.order.StatusDto;
import bookstore.exceptions.EntityNotFoundException;
import bookstore.mapper.OrderItemMapper;
import bookstore.mapper.OrderMapper;
import bookstore.mapper.StatusMapper;
import bookstore.model.Order;
import bookstore.model.OrderItem;
import bookstore.model.ShoppingCart;
import bookstore.model.User;
import bookstore.repository.order.OrderRepository;
import bookstore.repository.shoppingcart.ShoppingCartRepository;
import bookstore.repository.user.UserRepository;
import bookstore.service.OrderService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemMapper orderItemMapper;
    private final StatusMapper statusMapper;

    @Override
    public List<OrderDto> getOrderHistory(String email) {
        List<Order> ordersByUser = orderRepository.getOrderByUser(email);
        if (ordersByUser == null) {
            throw new RuntimeException("No orders found for email: " + email);
        }
        return ordersByUser.stream().map(orderMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public OrderDto createOrder(String email, String shippingAddress) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("cannot find user with email: " + email));
        ShoppingCart shoppingCart = shoppingCartRepository.getShoppingCartsByUser(email);
        if (shoppingCart == null) {
            throw new EntityNotFoundException("No shopping cart found for email: " + email);
        }
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setStatus(Order.Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
        Set<OrderItem> orderItems = shoppingCart.getCartItems().stream()
                .map(ci -> {
                    orderItemMapper.fromCartItem(ci);
                    OrderItem orderItem = orderItemMapper.fromCartItem(ci);
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toSet());
        BigDecimal totalPrice = orderItems.stream()
                .map(i -> i.getPrice()
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setOrderItems(orderItems);
        order.setTotal(totalPrice);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    public StatusDto updateOrderStatus(Long orderId, Order.Status status) {
        Order order = orderRepository.getOrderById(orderId);
        if (order == null) {
            throw new EntityNotFoundException("No order found for orderId: " + orderId);
        }
        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        return statusMapper.toDto(savedOrder);
    }
}
