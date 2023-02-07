package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.entities.order.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct,Integer> {
}
