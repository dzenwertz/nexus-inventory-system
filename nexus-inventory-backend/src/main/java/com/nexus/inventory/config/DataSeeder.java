package com.nexus.inventory.config;

import com.nexus.inventory.model.Category;
import com.nexus.inventory.model.Order;
import com.nexus.inventory.model.OrderItem;
import com.nexus.inventory.model.OrderStatus;
import com.nexus.inventory.model.Product;
import com.nexus.inventory.repository.CategoryRepository;
import com.nexus.inventory.repository.OrderRepository;
import com.nexus.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return;
        }

        Category electronics = categoryRepository.save(Category.builder().name("Electronics").description("High-tech gadgets").build());
        Category industrial = categoryRepository.save(Category.builder().name("Industrial").description("Supply chain hardware").build());
        Category logistics = categoryRepository.save(Category.builder().name("Logistics Equipment").description("Packaging & Transport").build());

        Product p1 = productRepository.save(Product.builder()
                .sku("SKU-NEX-001")
                .name("RFID Scanner Terminal X5")
                .price(new BigDecimal("499.99"))
                .stock(45)
                .minStockLevel(10)
                .category(electronics)
                .build());

        Product p2 = productRepository.save(Product.builder()
                .sku("SKU-NEX-002")
                .name("Industrial Thermal Printer P2")
                .price(new BigDecimal("899.50"))
                .stock(4)
                .minStockLevel(5)
                .category(industrial)
                .build());

        Product p3 = productRepository.save(Product.builder()
                .sku("SKU-NEX-003")
                .name("Smart Warehouse Conveyor Belt Module")
                .price(new BigDecimal("2499.00"))
                .stock(2)
                .minStockLevel(3)
                .category(industrial)
                .build());

        Product p4 = productRepository.save(Product.builder()
                .sku("SKU-NEX-004")
                .name("Heavy Duty Pallet Wrapper")
                .price(new BigDecimal("1250.00"))
                .stock(18)
                .minStockLevel(5)
                .category(logistics)
                .build());

        Product p5 = productRepository.save(Product.builder()
                .sku("SKU-NEX-005")
                .name("Barcode Label Roll (1000 Units)")
                .price(new BigDecimal("15.00"))
                .stock(0)
                .minStockLevel(50)
                .category(logistics)
                .build());

        Order order = Order.builder()
                .customerName("Acme Logistics Corp")
                .status(OrderStatus.PROCESSING)
                .totalAmount(new BigDecimal("999.98"))
                .build();

        OrderItem item1 = OrderItem.builder()
                .product(p1)
                .quantity(2)
                .unitPrice(p1.getPrice())
                .subtotal(new BigDecimal("999.98"))
                .build();

        order.addItem(item1);
        orderRepository.save(order);
        
        log.info("Database successfully seeded with sample data.");
    }
}
