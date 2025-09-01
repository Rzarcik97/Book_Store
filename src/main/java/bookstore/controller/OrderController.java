package bookstore.controller;

import bookstore.dto.order.OrderDto;
import bookstore.dto.order.StatusDto;
import bookstore.dto.orderitem.OrderItemDto;
import bookstore.model.Order;
import bookstore.service.OrderItemService;
import bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order_controller", description = "Order management application")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @GetMapping
    @Operation(summary = "Get all Orders from order history",
            description = "Get all Orders from Database related to your order history")
    public List<OrderDto> getOrderHistory(Authentication authentication) {
        String email = authentication.getName();
        return orderService.getOrderHistory(email);
    }

    @PostMapping
    @Operation(summary = "Place an order",
            description = "Place an order, the order will be created from"
                    + " your current shopping cart")
    public OrderDto createOrder(
            Authentication authentication,
            @RequestBody Map<String,String> shippingAddressRequest) {
        String email = authentication.getName();
        String shippingAddress = shippingAddressRequest.get("shippingAddress");
        return orderService.createOrder(email,shippingAddress);
    }

    @GetMapping("/{orderId}/items")
    @Operation(summary = "Get all order items by order id",
            description = "Get all order items by order id from Database")
    public Set<OrderItemDto> getOrderItemsByOrderId(Authentication authentication,
                                          @PathVariable Long orderId) {
        String email = authentication.getName();
        return orderItemService.getOrderItemsByOrderId(email, orderId);
    }

    @GetMapping("/{orderId}/items/{id}")
    @Operation(summary = "Get specific order items by order id and item id",
            description = "Get specific order items by order id and item id from Database")
    public OrderItemDto getOrderItemByOrderIdAndItemId(Authentication authentication,
                                            @PathVariable Long orderId,
                                            @PathVariable Long id) {
        String email = authentication.getName();
        return orderItemService.getOrderItemByOrderIdAndItemId(email, orderId, id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{orderId}")
    @Operation(summary = "Update order status by order id")
    public StatusDto updateOrderStatus(@PathVariable Long orderId,
                                       @RequestBody Map<String,Order.Status> status) {
        Order.Status orderStatus = status.get("status");
        return orderService.updateOrderStatus(orderId, orderStatus);
    }
}

