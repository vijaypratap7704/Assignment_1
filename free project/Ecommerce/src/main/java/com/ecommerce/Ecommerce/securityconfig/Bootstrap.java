package com.ecommerce.Ecommerce.securityconfig;

import com.ecommerce.Ecommerce.entities.user.Role;
import com.ecommerce.Ecommerce.entities.user.User;
import com.ecommerce.Ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.Arrays;

@Component
public class Bootstrap implements ApplicationRunner {

    @Autowired
    UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userRepository.count() < 1) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
         //   @Valid
            User admin = new User();
            admin.setFirstName("vijay");
            admin.setMiddleName("pratap");
            admin.setLastName("singh");
            admin.setEmail("vijaypratap@gmail.com");
            admin.setPassword(passwordEncoder.encode("77viJAY*("));
            admin.setActive(true);
            admin.setDeleted(false);

            Role role1 = new Role();

            role1.setAuthority("ROLE_ADMIN");

            admin.setRoleList(Arrays.asList(role1));

            role1.setUserList(Arrays.asList(admin));

            userRepository.save(admin);
        }
    }
}
