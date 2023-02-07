package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.entities.product.Category;
import com.ecommerce.Ecommerce.entities.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Integer> {
//    Category findById(int id);

}
