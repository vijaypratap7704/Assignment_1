package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.Exception.InvalidTokenException;
import com.ecommerce.Ecommerce.Exception.UserNotFoundException;
import com.ecommerce.Ecommerce.entities.user.TokenConfirm;
import com.ecommerce.Ecommerce.entities.user.User;
import com.ecommerce.Ecommerce.entitiesDto.PasswordDto;
import com.ecommerce.Ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.logging.Logger;

@RestController
public class UserController {

    Logger logger  = Logger.getLogger(UserController.class.toString());

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private UserService userService;

//    @Autowired
//    MessageSource messageSource;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/doLogout")
    public String logout(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            String tokenValue = authHeader.replace("Bearer", "").trim();
            OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
            tokenStore.removeAccessToken(accessToken);
        }
        return "Logged out successfully";
    }

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/admin/home")
    public String adminHome(){
        return "Admin home";
    }

    @GetMapping("/customer/home")
    public String customerHome(){
        return "Customer home";
    }

    @GetMapping("/seller/home")
    public String sellerHome(){
        return "Seller home";
    }

    @PostMapping("/forgotpassword")
    public ResponseEntity<Object> forgotPassword(@RequestParam("username") String username){
        logger.info("password forgot link sent");
        return userService.sendResetPasswordLinkToUser(username);
    }
    @PostMapping("/confirm-reset")
    public ResponseEntity<Object> validateResetToken(@RequestParam("token")String token,@Valid @RequestBody PasswordDto passwordDto)
            throws Exception {

        logger.info("password changed successfully");
        return userService.resetPasswordIfTokenIsValid(token, passwordDto.getPassword()
                ,passwordDto.getConfirmPassword());
    }
}