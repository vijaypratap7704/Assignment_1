package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    User findByEmail(String email);
    User getUserByEmail(String email);
}
