package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.entities.user.TokenConfirm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<TokenConfirm, Integer> {
    TokenConfirm findByToken(String confirmationToken);
    TokenConfirm findTokenByUserId(int id);
}