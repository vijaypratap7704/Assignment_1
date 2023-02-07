package com.ecommerce.Ecommerce.service;


import com.ecommerce.Ecommerce.Exception.CategoryIdException;
import com.ecommerce.Ecommerce.Exception.ProductIdException;
import com.ecommerce.Ecommerce.entities.order.Order;
import com.ecommerce.Ecommerce.entities.order.OrderProduct;
import com.ecommerce.Ecommerce.entities.order.OrderStatus;
import com.ecommerce.Ecommerce.entities.product.*;
//import com.ecommerce.Ecommerce.entities.product.ParentCategory;
import com.ecommerce.Ecommerce.entities.user.Customer;
import com.ecommerce.Ecommerce.entities.user.Seller;
import com.ecommerce.Ecommerce.Exception.UserNotFoundException;
import com.ecommerce.Ecommerce.entities.user.User;
import com.ecommerce.Ecommerce.entitiesDto.CategoryDto;
import com.ecommerce.Ecommerce.entitiesDto.CategoryMetaDataFieldValuesDto;
import com.ecommerce.Ecommerce.repository.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;


@Service
public class AdminService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryMetadataFieldRepository categoryMetadataFieldRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductVariationRepository productVariationRepository;

    @Autowired
    private CategoryMetadataFieldValuesRepository categoryMetadataFieldValuesRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private OrderRepository orderRepository;


    public AdminService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Iterable<Customer> getCustomer(int page,int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        Page<Customer> allCustomers = customerRepository.findAll(pageable);
        return allCustomers;
    }

    public Iterable<Seller> getSeller(int page,int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        Page<Seller> allSellers = sellerRepository.findAll(pageable);
        return allSellers;
    }

    public ResponseEntity<String> activateCustomer(int id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            Customer customer1 = customer.get();
            if (!customer1.isActive()) {
                customer1.setActive(true);
                customerRepository.save(customer1);
                userService.sendActivationLinkWithMessage1(customer1);
                return ResponseEntity.ok("Account is activated");
            } else {
                return ResponseEntity.ok("Acount is already active");
            }
        } else {
            throw new UserNotFoundException("Account not found");
        }
    }

    public ResponseEntity<String> deActivateCustomer(int id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            Customer customer1 = customer.get();
            if (customer1.isActive()) {
                customer1.setActive(false);
                customerRepository.save(customer1);
                userService.sendActivationLinkWithMessage2(customer1);
                return ResponseEntity.ok("Account is DeActivated");
            } else {
                return ResponseEntity.ok("Acount is already Deactive");
            }
        } else {
            throw new UserNotFoundException("Account not found");
        }
    }

    public ResponseEntity<String> activateSeller(int id) {
        Optional<Seller> seller = sellerRepository.findById(id);
        if (seller.isPresent()) {
            Seller seller1 = seller.get();
            if (!seller1.isActive()) {
                seller1.setActive(true);
                sellerRepository.save(seller1);
                userService.sendActivationLinkWithMessage1(seller1);
                return ResponseEntity.ok("Account is activated");
            } else {
                return ResponseEntity.ok("Acount is already active");
            }
        } else {
            throw new UserNotFoundException("Account not found");
        }
    }

    public ResponseEntity<String> deActivateSeller(int id) {
        Optional<Seller> seller = sellerRepository.findById(id);
        if (seller.isPresent()) {
            Seller seller1 = seller.get();
            if (seller1.isActive()) {
                seller1.setActive(false);
                userService.sendActivationLinkWithMessage2(seller1);
                return ResponseEntity.ok("Account is Deactived");
            } else {
                return ResponseEntity.ok("Acount is already Deactive");
            }
        } else {
            throw new UserNotFoundException("Account not found");
        }
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public ResponseEntity<String> addField(CategoryMetadataField categoryMetadataField) {
       // categoryMetadataFieldRepository.save(categoryMetadataField);
        categoryMetadataFieldRepository.save(categoryMetadataField);
        return ResponseEntity.ok("Field is Added");
    }

    public Iterable<CategoryMetadataField>findField(int page,int size)
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        return categoryMetadataFieldRepository.findAll(pageable);
    }

    public ResponseEntity<String> addCategoryField(CategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
            Optional<Category> category1 = categoryRepository.findById(categoryDto.getParentCategoryId());
            if(category1.isPresent())
                category.setParentCategory(category1.get());
        categoryRepository.save(category);
        return ResponseEntity.ok("Category Field is Added");
    }

    public Optional<Category> findCategory(int id) {
        return categoryRepository.findById(id);
    }

    public Iterable<Category> findCategories(int page,int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        return categoryRepository.findAll(pageable);
    }

    public ResponseEntity<String> updateCategory(int id, CategoryDto categoryDto) {
        Optional<Category> category1 = categoryRepository.findById(id);
        if (!category1.isPresent()) {
            throw new CategoryIdException("Category Id not found.");
        }
        if (categoryDto.getName().isEmpty()) {
            throw new CategoryIdException("Category Name must be non empty.");}
            category1.get().setName(categoryDto.getName());
            categoryRepository.save(category1.get());
            return ResponseEntity.ok("Category is updated successfully");
        }

        public ResponseEntity<String> addMetadataField ( CategoryMetaDataFieldValuesDto categoryMetadataFieldValuesDto){
            Optional<Category> category = categoryRepository.findById(categoryMetadataFieldValuesDto.getCategoryId());
            if (!category.isPresent()) {
                throw new CategoryIdException("Category Id not found.");
            }
            Optional<CategoryMetadataField> metadataField = categoryMetadataFieldRepository.findById(categoryMetadataFieldValuesDto.getMetaId());
            if (!metadataField.isPresent())
                throw new CategoryIdException("metadata Id not found.");
            CategoryMetadataFieldValues categoryMetadataFieldValues = new CategoryMetadataFieldValues();

            categoryMetadataFieldValues.setValue(String.join(", ", categoryMetadataFieldValuesDto.getValue()));
          //  CategoryMetadataField metadataField1 = metadataField.get();

            Category category1 = category.get();
          //  System.out.println("---->"+categoryMetadataFieldValues);
            categoryMetadataFieldValues.setCategory(category1);

          //  CategoryMetadataField Optional<categoryMetadataField> = categoryMetadataFieldRepository.findById(metaId);
            CategoryMetadataField categoryMetadataField1 = metadataField.get();
            categoryMetadataFieldValues.setCategorymetadatafield(categoryMetadataField1);
           // categoryMetadataFieldValues.setCategorymetadatafield(metadataField.get());

            categoryMetadataFieldValuesRepository.save(categoryMetadataFieldValues);
            return ResponseEntity.ok("metadata field value added successfully");
        }

    public ResponseEntity<String> updateMetadataField(CategoryMetaDataFieldValuesDto categoryMetadataFieldValuesDto) {
        Optional<Category> category = categoryRepository.findById(categoryMetadataFieldValuesDto.getCategoryId());
        if (!category.isPresent()) {
            throw new ProductIdException("Category Id not found.");
        }
        Optional<CategoryMetadataField> metadataField = categoryMetadataFieldRepository.findById(categoryMetadataFieldValuesDto.getMetaId());
        if(!metadataField.isPresent())
            throw new ProductIdException("metadata Id not found.");
        CategoryMetadataFieldValues categoryMetadataFieldValues = metadataField.get().getCategoryMetadataFieldValues();
        categoryMetadataFieldValues.setValue(String.join(", ", categoryMetadataFieldValuesDto.getValue()));
        CategoryMetadataField metadataField1 = metadataField.get();
        categoryMetadataFieldValues.setCategory(category.get());
        categoryMetadataFieldValues.setCategorymetadatafield(categoryMetadataFieldRepository.findById(categoryMetadataFieldValuesDto.getMetaId()).get());

        categoryMetadataFieldValuesRepository.save(categoryMetadataFieldValues);
        return ResponseEntity.ok("metadata field value update successfully");
    }
    ////////////////////////////////////////////////////////////////////////////////////

    public ResponseEntity<String> productActivate(int id) {
        Optional<Product> product = productRepository.findById(id);
        if(!(product.isPresent()))
            throw new ProductIdException("product Id not found.");
        else{
        Product product1 = product.get();
        product1.setActive(true);
        Seller seller = product1.getSeller();
        productRepository.save(product1);
                userService.sendProductActivationLinkWithMessage1(seller);
                return ResponseEntity.ok("Account is activated");
            }
    }

    public ResponseEntity<String> productDeactivate(int id) {
        Optional<Product> product = productRepository.findById(id);
        if(!(product.isPresent()))
            throw new ProductIdException("product Id not found.");
        else{
            Product product1 = product.get();
            product1.setActive(false);
            Seller seller = product1.getSeller();
            productRepository.save(product1);
            userService.sendProductActivationLinkWithMessage2(seller);
            return ResponseEntity.ok("Account is deactivated");
        }
    }

    public Optional<Product> viewProduct(int productId) {
        Optional<Product> product = productRepository.findById(productId);
        if(!(product.isPresent()))
            throw new ProductIdException("product Id not found.");
        return product;
    }

    public Iterable<Product> viewAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        return productRepository.findAll(pageable);
    }
    ////////////////////////////////////////////////////////////////////////////////////////

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

    public Iterable<Order> viewAllOrder(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        return orderRepository.findAll(pageable);
    }
}












