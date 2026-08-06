package com.nexus.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardMetricsDTO {
    private long totalProducts;
    private long totalStockUnits;
    private long activeOrders;
    private long lowStockCount;
    private List<ProductDTO> lowStockAlerts;
}
