package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.entities.user.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Integer> {
    Page<Seller> findAll(Pageable pageable);
    Seller findByEmail(String email);
}
