package com.nexus.inventory.repository;

import com.nexus.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    @Query("SELECT p FROM Product p WHERE p.stock <= p.minStockLevel")
    List<Product> findLowStockProducts();

    @Query("SELECT COALESCE(SUM(p.stock), 0) FROM Product p")
    Long countTotalStockUnits();

    List<Product> findByNameContainingIgnoreCase(String query);
}
