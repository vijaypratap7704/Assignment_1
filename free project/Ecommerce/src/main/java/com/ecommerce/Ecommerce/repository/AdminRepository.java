package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.entities.user.User;
import org.springframework.data.repository.CrudRepository;

public interface AdminRepository extends CrudRepository<User,Integer> {
}
