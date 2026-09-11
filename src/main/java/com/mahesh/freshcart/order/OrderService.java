package com.mahesh.freshcart.order;

import com.mahesh.freshcart.inventory.Inventory;
import com.mahesh.freshcart.inventory.InventoryRepository;
import com.mahesh.freshcart.product.Product;
import com.mahesh.freshcart.product.ProductRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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


    // CREATE ORDER
    @Transactional
    public Order saveOrder(Order order) {

        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);

        BigDecimal totalAmount = BigDecimal.ZERO;

        if (order.getItems() != null) {

            for (OrderItem item : order.getItems()) {

                // Check product exists
                Product product = productRepository
                        .findById(item.getProductId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Product not found"
                        ));


                // Find inventory for product
                Inventory inventory = inventoryRepository
                        .findByProductId(item.getProductId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Inventory not found"
                        ));


                // Check enough stock
                if (inventory.getStockQuantity() < item.getQuantity()) {

                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Insufficient stock"
                    );
                }


                // Reduce inventory
                inventory.setStockQuantity(
                        inventory.getStockQuantity() - item.getQuantity()
                );

                inventoryRepository.save(inventory);


                // Get real product price
                item.setPrice(product.getPrice());


                // Connect OrderItem to Order
                item.setOrder(order);


                // Calculate item total
                BigDecimal itemTotal = product.getPrice()
                        .multiply(
                                BigDecimal.valueOf(item.getQuantity())
                        );


                // Add item total to order total
                totalAmount = totalAmount.add(itemTotal);
            }


            // Set final order total
            order.setTotalAmount(totalAmount);
        }


        return orderRepository.save(order);
    }


    // GET ALL ORDERS
    public List<Order> getAllOrders() {

        return orderRepository.findAll();
    }


    // GET ORDER BY ID
    public Order getOrderById(Long id) {

        return orderRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found"
                ));
    }


    // NEW STATUS-ONLY UPDATE
    @Transactional
    public Order updateOrderStatus(
            Long id,
            OrderStatus newStatus) {

        Order existingOrder = getOrderById(id);


        // A cancelled order cannot become active again
        if (existingOrder.getStatus() == OrderStatus.CANCELLED
                && newStatus != OrderStatus.CANCELLED) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cancelled order cannot be updated"
            );
        }


        // Restore stock only the first time the order is cancelled
        if (newStatus == OrderStatus.CANCELLED
                && existingOrder.getStatus() != OrderStatus.CANCELLED) {

            for (OrderItem item : existingOrder.getItems()) {

                Inventory inventory = inventoryRepository
                        .findByProductId(item.getProductId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Inventory not found"
                        ));


                inventory.setStockQuantity(
                        inventory.getStockQuantity() + item.getQuantity()
                );


                inventoryRepository.save(inventory);
            }
        }


        existingOrder.setStatus(newStatus);


        return orderRepository.save(existingOrder);
    }


    // DELETE ORDER
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