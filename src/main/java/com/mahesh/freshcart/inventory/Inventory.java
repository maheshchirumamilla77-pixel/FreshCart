package com.mahesh.freshcart.inventory;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;
@Entity
public class Inventory {
    @Id
    @GeneratedValue

    private Long id;
    @Positive(message = "Product ID must be greater than 0")
    private Long productId;
    @PositiveOrZero(message = "Stock quantity cannot be negative")
    private Integer stockQuantity;
    @PositiveOrZero(message = "Reorder level cannot be negative")
    private Integer reorderLevel;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    public Integer getReorderLevel() {
        return reorderLevel;
    }
    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }
}
