package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.entities.user.Customer;
import org.hibernate.sql.Delete;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Page<Customer> findAll(Pageable pageable);
    Customer findByEmail(String email);
    @Query(value = "delete from address Where id=id",nativeQuery = true)
    void deleteAddress(long id);

   // List<Customer> findAll(Pageable pageable);
}
