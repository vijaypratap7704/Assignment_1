package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.entities.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Integer> {

    @Query(value="select * from orders where customer_id=:id",nativeQuery = true)
    List<Order> findByCustomerId(@Param("id") int id);

//    @Query(value="select * from orders where customer_id=:id And id=:id",nativeQuery = true)
//    Optional<Order> findByOrderId(int customerId,@Param("id")int id);

}
