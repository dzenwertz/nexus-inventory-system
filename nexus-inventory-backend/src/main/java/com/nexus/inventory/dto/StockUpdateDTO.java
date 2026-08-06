package com.nexus.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockUpdateDTO {

    @NotNull(message = "New stock quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
}
