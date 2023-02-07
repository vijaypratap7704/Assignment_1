package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.entities.order.Cart;
import com.ecommerce.Ecommerce.entities.order.CustomerProductVariation;
import com.ecommerce.Ecommerce.entities.product.ProductVariation;
import com.ecommerce.Ecommerce.entities.user.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,CustomerProductVariation> {

//    List<Cart> findByCustomer(Customer customer);
//
//    Cart findByProductVariation(ProductVariation productVariation);

    @Query(value = "Select * from cart where customer_id=:customerId",nativeQuery = true)
    List<Cart> findByCustomerId(@Param("customerId") int  customerId);

    @Query(value = "Select * from cart where product_variation_id=:variationId",nativeQuery = true)
    Optional<Cart> findByProductVariationId(@Param("variationId") int  variationId);

    @Modifying
    @Query(value="Delete from cart where customer_id=:customerId AND product_variation_id=:variationId",nativeQuery = true)
    void DeleteCart(@Param("customerId") int  customerId,@Param("variationId") int  variationId);

    //Cart findByCustomerAndProductVariation(Customer customer, ProductVariation productVariation);
}
