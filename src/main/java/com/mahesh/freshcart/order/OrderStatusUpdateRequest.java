package com.mahesh.freshcart.order;
import jakarta.validation.constraints.NotNull;

public class OrderStatusUpdateRequest {

@NotNull(message = "Order status is required")
    private OrderStatus status;

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}