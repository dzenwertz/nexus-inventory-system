package com.nexus.inventory.service;

import com.nexus.inventory.dto.ProductDTO;
import com.nexus.inventory.dto.StockUpdateDTO;
import com.nexus.inventory.exception.ResourceNotFoundException;
import com.nexus.inventory.model.Category;
import com.nexus.inventory.model.Product;
import com.nexus.inventory.repository.CategoryRepository;
import com.nexus.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts(String search) {
        List<Product> products;
        if (search != null && !search.trim().isEmpty()) {
            products = productRepository.findByNameContainingIgnoreCase(search.trim());
        } else {
            products = productRepository.findAll();
        }
        return products.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToDTO(product);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getLowStockProducts() {
        return productRepository.findLowStockProducts().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO dto) {
        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));
        }

        Product product = Product.builder()
                .sku(dto.getSku())
                .name(dto.getName())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .minStockLevel(dto.getMinStockLevel())
                .category(category)
                .build();

        Product saved = productRepository.save(product);
        return mapToDTO(saved);
    }

    @Transactional
    public ProductDTO updateStock(Long id, StockUpdateDTO stockUpdateDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setStock(stockUpdateDTO.getStock());
        Product updated = productRepository.save(product);
        return mapToDTO(updated);
    }

    private ProductDTO mapToDTO(Product product) {
        String stockStatus;
        if (product.getStock() == 0) {
            stockStatus = "OUT_OF_STOCK";
        } else if (product.getStock() <= product.getMinStockLevel()) {
            stockStatus = "LOW";
        } else {
            stockStatus = "SUFFICIENT";
        }

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
