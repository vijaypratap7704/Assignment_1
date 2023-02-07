package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.Exception.AlreadyActiveAccountException;
import com.ecommerce.Ecommerce.Exception.InvalidTokenException;
import com.ecommerce.Ecommerce.Exception.UserInactiveException;
import com.ecommerce.Ecommerce.Exception.UserNotFoundException;
import com.ecommerce.Ecommerce.entities.user.TokenConfirm;
import com.ecommerce.Ecommerce.entities.user.Customer;
import com.ecommerce.Ecommerce.entities.user.Seller;
import com.ecommerce.Ecommerce.entities.user.User;
import com.ecommerce.Ecommerce.repository.CustomerRepository;
import com.ecommerce.Ecommerce.repository.TokenRepository;
import com.ecommerce.Ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.time.LocalDateTime;


@Service
@Transactional
public class UserService {
    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;


    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public void sendActivationLinkCustomer(Customer customer) {
        TokenConfirm confirmationToken = new TokenConfirm(customer);
        tokenRepository.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(customer.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setText("To confirm your account, please click here : "
                + "http://localhost:8080/confirm-account?token=" + confirmationToken.getToken());
        emailService.sendEmail(mailMessage);
    }

    public void sendActivationLinkWithSeller(Seller seller) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(seller.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setText("Your account has been created,waiting for Approval. ");
        emailService.sendEmail(mailMessage);
    }

    public void sendActivationLinkWithMessage1(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Activated");
        mailMessage.setText("Congratulation Your Account is activated");
        emailService.sendEmail(mailMessage);
    }
    public void sendActivationLinkWithMessage2(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Deactivated");
        mailMessage.setText("Your Account is Deactivated");
        emailService.sendEmail(mailMessage);
    }
    public void sendProductActivationLinkWithMessage1(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Activated");
        mailMessage.setText("Congratulation Your Product is activated");
        emailService.sendEmail(mailMessage);
    }
    public void sendProductActivationLinkWithMessage2(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Deactivated");
        mailMessage.setText("Your Product is Deactivated");
        emailService.sendEmail(mailMessage);
    }


    public String validateVerificationTokenAndSaveUser(String token) {
        final TokenConfirm confirmationToken = tokenRepository.findByToken(token);
        if (confirmationToken == null) {
            return "invalidToken";
        }

        final User user = confirmationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((confirmationToken.getExpiryDate()
                .getTime() - cal.getTime()
                .getTime()) <= 0) {
            tokenRepository.delete(confirmationToken);
            return "expired";
        }

        user.setActive(true);
        userRepository.save(user);
        tokenRepository.delete(confirmationToken);
        return "tokenvalid";
    }

    public ResponseEntity<Object> sendResetPasswordLinkToUser(String username) {

//System.out.println(username);
        User user = userRepository.getUserByEmail(username);

        if(user!=null)
        {
            TokenConfirm confirmationToken = new TokenConfirm(user);
            tokenRepository.save(confirmationToken);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(username);

            mailMessage.setSubject("Complete Password Reset!");
            mailMessage.setText("To complete the password reset process," +
                    " please click here and enter new password \n: "
                    + "http://localhost:8080/confirm-reset?token="+confirmationToken.getToken());

            emailService.sendEmail(mailMessage);

            return new ResponseEntity("Password Link sent, please check your email",
                    HttpStatus.OK);
        }
        else
        {
            throw new UserNotFoundException("User not found");
        }
    }

    public ResponseEntity<Object> resetPasswordIfTokenIsValid(String token, String newPassword,
                                                              String confirmNewPassword) {

//System.out.println(token);
           String tokenValidationResponse=verifyToken(token);
          if (tokenValidationResponse.equals("tokenValid")) {

        TokenConfirm confirmationToken = tokenRepository.findByToken(token);
        System.out.println(confirmationToken.getUser());
        User user = confirmationToken.getUser();
        if (user.isActive()) {

            if (!(newPassword.equals(confirmNewPassword))) {
                throw new InvalidTokenException(
                        "Enter same password in both password and confirm password field");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            return new ResponseEntity<Object>("Password Changed Successfully"
                    , HttpStatus.OK);
        } else {
            user.setActive(true);
            userRepository.save(user);
            throw new UserInactiveException("First make User activated, try again");
        }

          }
        else {
            throw new InvalidTokenException("Token is invalid");
        }
    }

        public String verifyToken(String token) {
            TokenConfirm confirmationToken = tokenRepository.findByToken(token);
            if(confirmationToken.getUser().isActive())
            {

            }
            if (confirmationToken == null) {
                return "invalidToken";
            }

            final User user = confirmationToken.getUser();
            LocalDateTime localDateTime = LocalDateTime.now();
            Date dateFromLocalDT = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            if (confirmationToken.getExpiryDate().compareTo(dateFromLocalDT) < 0) {
                throw new InvalidTokenException("Token is expired");
            } else {
                return "tokenValid";
            }
        }

    public void sendResetPasswordMessage(Customer customer) {
        throw new AlreadyActiveAccountException("Already activated account");
    }

    public void manageAttempts(String username){
        User user = userRepository.findByEmail(username);
        if(user!=null) {
            if (user.getAttempts() > 2) {
                if(user.isNonLocked() == true){
                    user.setNonLocked(false);
                    SimpleMailMessage mail = new SimpleMailMessage();
                    mail.setTo(user.getEmail());
                    mail.setSubject("Account locked");
                    mail.setText("Your account is Locked");
                    emailService.sendEmail(mail);
                }
            }
            user.setAttempts(user.getAttempts() + 1);
            userRepository.save(user);
        }
    }
}

