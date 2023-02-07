package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.entities.user.Role;
import com.ecommerce.Ecommerce.entities.user.User;
import com.ecommerce.Ecommerce.repository.UserRepository;
import com.ecommerce.Ecommerce.securityconfig.AppUser;
import com.ecommerce.Ecommerce.securityconfig.GrantAuthorityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDao {

    @Autowired
    UserRepository userRepository;

    @Transactional
    AppUser loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email);
        if (email != null) {
            List<GrantAuthorityImpl> grantAuthorityList=new ArrayList<>();
            List<Role> roles=user.getRoleList();
            for(Role role: roles)
            {
                grantAuthorityList.add(new GrantAuthorityImpl(role.getAuthority()));
            }
            return new AppUser(user.getEmail(), user.getPassword(),user.isActive(),user.isNonLocked(),grantAuthorityList);
        }
        else {
            throw new RuntimeException();
        }

    }
}
