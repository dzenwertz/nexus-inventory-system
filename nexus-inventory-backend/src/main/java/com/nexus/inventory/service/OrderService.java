package com.nexus.inventory.service;

import com.nexus.inventory.dto.*;
import com.nexus.inventory.exception.InsufficientStockException;
import com.nexus.inventory.exception.InvalidOrderException;
import com.nexus.inventory.exception.ResourceNotFoundException;
import com.nexus.inventory.model.*;
import com.nexus.inventory.repository.OrderRepository;
import com.nexus.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new InvalidOrderException("Order must contain at least one item");
        }

        Order order = Order.builder()
                .customerName(request.getCustomerName())
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.getProductId()));

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(
                        String.format("Insufficient stock for product '%s'. Requested: %d, Available: %d",
                                product.getName(), itemRequest.getQuantity(), product.getStock())
                );
            }

            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .subtotal(subtotal)
                    .build();

            order.addItem(item);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders(OrderStatus status) {
        List<Order> orders;
        if (status != null) {
            orders = orderRepository.findByStatus(status);
        } else {
            orders = orderRepository.findAll();
        }
        return orders.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return mapToResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        OrderStatus currentStatus = order.getStatus();

        if (currentStatus == OrderStatus.CANCELLED) {
            throw new InvalidOrderException("Cannot change status of a CANCELLED order");
        }

        if (currentStatus == OrderStatus.COMPLETED && newStatus == OrderStatus.CANCELLED) {
            throw new InvalidOrderException("Cannot cancel an order that is already COMPLETED");
        }

        if (newStatus == OrderStatus.CANCELLED && currentStatus != OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return mapToResponse(updatedOrder);
    }

    @Transactional(readOnly = true)
    public DashboardMetricsDTO getDashboardMetrics() {
        long totalProducts = productRepository.count();
        Long totalStockUnits = productRepository.countTotalStockUnits();
        long activeOrders = orderRepository.countByStatusIn(Arrays.asList(OrderStatus.PENDING, OrderStatus.PROCESSING));

        List<Product> lowStockProducts = productRepository.findLowStockProducts();
        long lowStockCount = lowStockProducts.size();

        List<ProductDTO> lowStockAlerts = lowStockProducts.stream()
                .map(this::mapProductToDTO)
                .collect(Collectors.toList());

        return DashboardMetricsDTO.builder()
                .totalProducts(totalProducts)
                .totalStockUnits(totalStockUnits != null ? totalStockUnits : 0)
                .activeOrders(activeOrders)
                .lowStockCount(lowStockCount)
                .lowStockAlerts(lowStockAlerts)
                .build();
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productSku(item.getProduct().getSku())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .build();
    }

    private ProductDTO mapProductToDTO(Product product) {
        String stockStatus = product.getStock() == 0 ? "OUT_OF_STOCK" :
                (product.getStock() <= product.getMinStockLevel() ? "LOW" : "SUFFICIENT");

        return ProductDTO.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .minStockLevel(product.getMinStockLevel())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : "Uncategorized")
                .stockStatus(stockStatus)
                .build();
    }
}
