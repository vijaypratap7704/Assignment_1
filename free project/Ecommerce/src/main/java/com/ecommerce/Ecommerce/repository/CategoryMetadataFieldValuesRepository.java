package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.entities.product.CategoryMetadataFieldValues;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryMetadataFieldValuesRepository extends JpaRepository<CategoryMetadataFieldValues,Integer> {

}
