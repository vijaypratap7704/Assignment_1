package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.entities.product.ProductVariation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariationRepository extends JpaRepository<ProductVariation,Integer> {

}
