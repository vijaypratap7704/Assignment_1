package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.Exception.CategoryIdException;
import com.ecommerce.Ecommerce.Exception.EmailExistsException;
import com.ecommerce.Ecommerce.Exception.ProductIdException;
import com.ecommerce.Ecommerce.Exception.UserNotFoundException;
import com.ecommerce.Ecommerce.entities.order.Order;
import com.ecommerce.Ecommerce.entities.order.OrderProduct;
import com.ecommerce.Ecommerce.entities.order.OrderStatus;
import com.ecommerce.Ecommerce.entities.order.Status;
import com.ecommerce.Ecommerce.entities.product.*;
import com.ecommerce.Ecommerce.entities.user.Address;
import com.ecommerce.Ecommerce.entities.user.Customer;
import com.ecommerce.Ecommerce.entities.user.Role;
import com.ecommerce.Ecommerce.entities.user.Seller;
import com.ecommerce.Ecommerce.entitiesDto.UpdatePasswordDto;
import com.ecommerce.Ecommerce.repository.*;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;
import java.util.stream.Collectors;






@Service
public class SellerService {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ProductVariationRepository productVariationRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    public boolean checkIfUserExist(String email) {
        return sellerRepository.findByEmail(email) != null;
    }


    public void register(@Valid Seller seller) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (checkIfUserExist(seller.getEmail())) {
            throw new EmailExistsException("Email already registered");
        }
        Role role = new Role();
        role.setAuthority("ROLE_SELLER");
        seller.setPassword(passwordEncoder.encode(seller.getPassword()));
        seller.setRoleList(Arrays.asList(role));
        seller.setAddress(seller.getAddress());
        Address address = seller.getAddress();
        address.setSeller(seller);
        sellerRepository.save(seller);
        userService.sendActivationLinkWithSeller(seller);
    }

    public Seller find(String email) {
        Seller seller = sellerRepository.findByEmail(email);
        return seller;
    }

    public void update(String email, Seller seller) {
        if(checkIfUserExist(email)) {
            Seller seller1 = sellerRepository.findByEmail(email);
            //    seller1.setAddress(seller.getAddress());
            seller1.setContact(seller.getContact());
            seller1.setFirstName(seller.getFirstName());
            seller1.setMiddleName(seller.getMiddleName());
            seller1.setLastName(seller.getLastName());
            sellerRepository.save(seller1);
        }
        else{
            throw new UserNotFoundException("Email not valid");
        }
    }

    public void updatePassword(String email, UpdatePasswordDto updatePasswordDto) {
        if(checkIfUserExist(email)) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Seller seller1 = sellerRepository.findByEmail(email);
        seller1.setPassword(passwordEncoder.encode(updatePasswordDto.getPassword()));
        sellerRepository.save(seller1);
        }
        else{
            throw new UserNotFoundException("Email not valid");
        }
    }

    public void updateAddress(Long id, String email, Address address) {
        if(checkIfUserExist(email)) {
        Seller seller = sellerRepository.findByEmail(email);
        Address address1 = seller.getAddress();
            if (!addressRepository.findById(id).isPresent())
                throw new UserNotFoundException("Address id is not present");
            else {
                if (address1.getId() == id) {
                    address1.setAddressLine(address.getAddressLine());
                    address1.setCity(address.getCity());
                    address1.setCountry(address.getCountry());
                    address1.setLabel(address.getLabel());
                    address1.setState(address.getState());
                    address1.setZipCode(address.getZipCode());
                }
                sellerRepository.save(seller);
            }
    }
        else{
        throw new UserNotFoundException("Email not valid");
    }
    }

    public Iterable<Category> showAll(int page,int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        return categoryRepository.findAll();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void addProduct(ProductDto productDto, String email) {
        Seller seller = sellerRepository.findByEmail(email);
        Optional<Category> category = categoryRepository.findById(productDto.getCategoryId());
        if (!category.isPresent()) {
            throw new CategoryIdException("Category Id not found.");
        }
        Category category1 = category.get();
        Product product = new Product();
        ModelMapper mm = new ModelMapper();
        mm.getConfiguration().setAmbiguityIgnored(true);
        mm.map(productDto, product);
        category1.setProductList(List.of(product));
        seller.setProductList(List.of(product));
        product.setCategory(category1);
        product.setSeller(seller);
        productRepository.save(product);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(email);
        mailMessage.setTo("vijaypratap@gmail.com");
        mailMessage.setSubject("Complete Activation for product!");
        mailMessage.setText("To activate this product," + product.toString() + " please click here : "
                + "http://localhost:8080/activate/product?id="+product.getId());
        emailService.sendEmail(mailMessage);
    }

    public void productActivate(int id) {
        Optional<Product> product = productRepository.findById(id);
        if(!(product.isPresent()))
            throw new ProductIdException("product Id not found.");
        Product product1 = product.get();
        product1.setActive(true);
        productRepository.save(product1);
    }
    public Optional<Product> viewProduct(int productId) {
        Optional<Product> product = productRepository.findById(productId);
        if(!(product.isPresent()))
            throw new ProductIdException("product Id not found.");
        return product;
    }

    public Iterable<Product> viewAll(int page,int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        return productRepository.findAll(pageable);
    }

    public void deleteProductById(int id) {
        Optional<Product> product = productRepository.findById(id);
        if(!(product.isPresent()))
            throw new ProductIdException("product Id not found.");
        Product product1 = product.get();
        product1.setSeller(null);
        product1.setCategory(null);
        productRepository.delete(product1);
    }

    public void updatingProduct(ProductDto productDto,int id) {
        Optional<Product> product = productRepository.findById(id);
        if(!(product.isPresent()))
            throw new ProductIdException("product Id not found.");
        Product product1 = product.get();
        ModelMapper mm = new ModelMapper();
        mm.getConfiguration().setAmbiguityIgnored(true);
        mm.map(productDto, product1);
        productRepository.save(product1);
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void addVariationProduct(ProductVariationDto productVariationDto)
    {
        ProductVariation variation = new ProductVariation();
        int product = productVariationDto.getId();
        Optional<Product> product1 = productRepository.findById(product);
        if(!product1.isPresent())
            throw new ProductIdException("product Id not found");
        variation.setQuantityAvailable(productVariationDto.getQuantityAvailable());
        variation.setPrice(productVariationDto.getPrice());
        variation.setPrimaryImageName(productVariationDto.getPrimaryImageName());
        JSONObject jsonObject = new JSONObject(productVariationDto.getMetadata());
        variation.setMetadata(jsonObject);
        product1.get().addProductVariation(variation);
        productRepository.save(product1.get());
    }

    public Optional<ProductVariation> viewVariationProduct(int id) {
        Optional<ProductVariation> productVariation = productVariationRepository.findById(id);
        if(!(productVariation.isPresent()))
            throw new ProductIdException("product variation Id not found.");
        return productVariation;
    }

    public Iterable<ProductVariation> viewAllvariationProduct(int page,int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        return productVariationRepository.findAll();
    }

    public void updatingVariationProduct(ProductVariationDto productVariationDto, int id) {
        Optional<ProductVariation> productVariation = productVariationRepository.findById(id);
        if(!productVariation.isPresent())
            throw new ProductIdException("product variation Id not found");
        ProductVariation variation = productVariation.get();
        int product = productVariationDto.getId();
        Optional<Product> product1 = productRepository.findById(product);
        if(!product1.isPresent())
            throw new ProductIdException("product Id not found");
        variation.setQuantityAvailable(productVariationDto.getQuantityAvailable());
        variation.setPrice(productVariationDto.getPrice());
        variation.setPrimaryImageName(productVariationDto.getPrimaryImageName());
        JSONObject jsonObject = new JSONObject(productVariationDto.getMetadata());
        variation.setMetadata(jsonObject);
        product1.get().addProductVariation(variation);
        productRepository.save(product1.get());



    }


//    public List<Order> findAllOrders(String name) {
//        List<Order> orders = orderRepository.findAll();
//        List<Order> orderList = new ArrayList<>();
//        Seller seller = sellerRepository.findByEmail(name);
//        for(Order order:orders){
//            if(order.get)
//        }
//    }


    public void cancelOrder(int orderProductId, OrderStatus orderStatus, String email){
        Optional<OrderProduct> orderProduct1 = orderProductRepository.findById(orderProductId);
        if(!orderProduct1.isPresent())
            throw new CategoryIdException("Order Product Id not present");

        OrderStatus status = new OrderStatus();
//            status.setFromStatus(Status.ORDER_PLACED);
//            status.setToStatus(Status.CANCELLED);
status.setFromStatus(orderStatus.getFromStatus());
status.setToStatus(orderStatus.getToStatus());
//status.setTransitionNotesComments(orderStatus.getTransitionNotesComments());
            orderProduct1.get().setOrderStatus(status);
            orderProductRepository.save(orderProduct1.get());
    }
}
