package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.entities.user.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address,Long> {
}
