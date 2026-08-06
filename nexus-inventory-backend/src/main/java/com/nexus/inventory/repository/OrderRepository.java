package com.nexus.inventory.repository;

import com.nexus.inventory.model.Order;
import com.nexus.inventory.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatus(OrderStatus status);

    long countByStatusIn(List<OrderStatus> statuses);
}
