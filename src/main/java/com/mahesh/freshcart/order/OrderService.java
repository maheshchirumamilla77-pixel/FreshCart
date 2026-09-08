package com.mahesh.freshcart.order;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.mahesh.freshcart.product.Product;
import com.mahesh.freshcart.product.ProductRepository;
import com.mahesh.freshcart.inventory.InventoryRepository;
import com.mahesh.freshcart.inventory.Inventory;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            InventoryRepository inventoryRepository) {

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }
@Transactional
    public Order saveOrder(Order order) {

        order.setOrderDate(LocalDateTime.now());

    order.setStatus("CREATED");

        BigDecimal totalAmount = BigDecimal.ZERO;

        if (order.getItems() != null) {

            for (OrderItem item : order.getItems()) {
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Product not found"
                        ));

                Inventory inventory = inventoryRepository.findByProductId(item.getProductId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Inventory not found"
                        ));

                if (inventory.getStockQuantity() < item.getQuantity()) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Insufficient stock"
                    );
                }
                inventory.setStockQuantity(
                        inventory.getStockQuantity() - item.getQuantity()
                );

                inventoryRepository.save(inventory);

                item.setPrice(product.getPrice());

                item.setOrder(order);

                BigDecimal itemTotal = product.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));

                totalAmount = totalAmount.add(itemTotal);
            }
            order.setTotalAmount(totalAmount);
        }

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found"
                ));
    }
    public Order updateOrder(Long id, Order updatedOrder) {
        Order existingOrder = getOrderById(id);

        existingOrder.setCustomerName(updatedOrder.getCustomerName());
        existingOrder.setTotalAmount(updatedOrder.getTotalAmount());
        existingOrder.setStatus(updatedOrder.getStatus());

        return orderRepository.save(existingOrder);

    }
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResponseStatusException(
             HttpStatus.NOT_FOUND,
             "Order not found"
            );
        }
        orderRepository.deleteById(id);
    }
}