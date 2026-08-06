package com.nexus.inventory.service;

import com.nexus.inventory.dto.CreateOrderRequest;
import com.nexus.inventory.dto.OrderItemRequest;
import com.nexus.inventory.dto.OrderResponse;
import com.nexus.inventory.exception.InsufficientStockException;
import com.nexus.inventory.exception.InvalidOrderException;
import com.nexus.inventory.exception.ResourceNotFoundException;
import com.nexus.inventory.model.Order;
import com.nexus.inventory.model.OrderItem;
import com.nexus.inventory.model.OrderStatus;
import com.nexus.inventory.model.Product;
import com.nexus.inventory.repository.OrderRepository;
import com.nexus.inventory.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .sku("SKU-TEST-1")
                .name("Test Industrial Scanner")
                .price(new BigDecimal("100.00"))
                .stock(10)
                .minStockLevel(2)
                .build();
    }

    @Test
    @DisplayName("Should create order successfully and deduct stock")
    void testCreateOrder_Success_DeductsStock() {
        // Given
        OrderItemRequest itemRequest = OrderItemRequest.builder()
                .productId(1L)
                .quantity(3)
                .build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerName("TechCorp Global")
                .items(List.of(itemRequest))
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        // When
        OrderResponse response = orderService.createOrder(request);

        // Then
        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("TechCorp Global", response.getCustomerName());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        assertEquals(new BigDecimal("300.00"), response.getTotalAmount());
        assertEquals(7, testProduct.getStock(), "Stock should be deducted from 10 to 7");

        verify(productRepository).save(testProduct);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when requested quantity exceeds available stock")
    void testCreateOrder_InsufficientStock_ThrowsException() {
        // Given
        OrderItemRequest itemRequest = OrderItemRequest.builder()
                .productId(1L)
                .quantity(15)
                .build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerName("Global Supply Co")
                .items(List.of(itemRequest))
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When & Then
        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> orderService.createOrder(request)
        );

        assertTrue(exception.getMessage().contains("Insufficient stock"));
        assertEquals(10, testProduct.getStock(), "Stock should remain unchanged");
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product does not exist")
    void testCreateOrder_ProductNotFound_ThrowsException() {
        // Given
        OrderItemRequest itemRequest = OrderItemRequest.builder()
                .productId(99L)
                .quantity(1)
                .build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerName("Unknown Client")
                .items(List.of(itemRequest))
                .build();

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.createOrder(request)
        );

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should restore product stock when an order is CANCELLED")
    void testUpdateOrderStatus_ToCancelled_RestoresStock() {
        // Given
        testProduct.setStock(5);

        OrderItem orderItem = OrderItem.builder()
                .id(10L)
                .product(testProduct)
                .quantity(5)
                .unitPrice(new BigDecimal("100.00"))
                .subtotal(new BigDecimal("500.00"))
                .build();

        Order existingOrder = Order.builder()
                .id(50L)
                .customerName("Cancel Test Client")
                .status(OrderStatus.PENDING)
                .totalAmount(new BigDecimal("500.00"))
                .items(List.of(orderItem))
                .build();

        orderItem.setOrder(existingOrder);

        when(orderRepository.findById(50L)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        OrderResponse response = orderService.updateOrderStatus(50L, OrderStatus.CANCELLED);

        // Then
        assertEquals(OrderStatus.CANCELLED, response.getStatus());
        assertEquals(10, testProduct.getStock(), "Stock should be restored from 5 to 10");
        verify(productRepository).save(testProduct);
        verify(orderRepository).save(existingOrder);
    }

    @Test
    @DisplayName("Should throw InvalidOrderException when trying to cancel an already COMPLETED order")
    void testUpdateOrderStatus_CompletedToCancelled_ThrowsException() {
        // Given
        Order completedOrder = Order.builder()
                .id(50L)
                .customerName("Completed Client")
                .status(OrderStatus.COMPLETED)
                .totalAmount(new BigDecimal("200.00"))
                .items(Collections.emptyList())
                .build();

        when(orderRepository.findById(50L)).thenReturn(Optional.of(completedOrder));

        // When & Then
        InvalidOrderException exception = assertThrows(
                InvalidOrderException.class,
                () -> orderService.updateOrderStatus(50L, OrderStatus.CANCELLED)
        );

        assertTrue(exception.getMessage().contains("Cannot cancel an order that is already COMPLETED"));
        verify(orderRepository, never()).save(any(Order.class));
    }
}
