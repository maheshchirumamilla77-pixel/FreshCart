package com.mahesh.freshcart;


import com.mahesh.freshcart.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
