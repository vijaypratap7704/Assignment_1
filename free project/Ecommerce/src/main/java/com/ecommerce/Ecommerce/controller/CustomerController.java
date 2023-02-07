package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.Exception.EmailExistsException;
import com.ecommerce.Ecommerce.entities.order.Cart;
import com.ecommerce.Ecommerce.entities.order.Order;
import com.ecommerce.Ecommerce.entities.order.OrderStatus;
import com.ecommerce.Ecommerce.entities.product.Category;
import com.ecommerce.Ecommerce.entities.product.Product;
import com.ecommerce.Ecommerce.entities.user.Address;
import com.ecommerce.Ecommerce.entities.user.Customer;
import com.ecommerce.Ecommerce.entities.user.User;
import com.ecommerce.Ecommerce.entitiesDto.CartDto;
import com.ecommerce.Ecommerce.entitiesDto.PartialOrderDto;
import com.ecommerce.Ecommerce.entitiesDto.UpdatePasswordDto;
import com.ecommerce.Ecommerce.service.CustomerService;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
public class CustomerController {
    Logger logger = Logger.getLogger(CustomerController.class.toString());

    @Autowired
    private CustomerService customerService;

//    @Autowired
//    public CustomerController(CustomerService customerService) {
//        this.customerService = customerService;
//    }

    @PostMapping(value="/register",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> registerCustomer(@Valid @RequestBody Customer customer, final HttpServletRequest request) {
        if(!customer.getPassword().equals(customer.getConfirmPassword())){
            return new ResponseEntity<>("Password and confirm password not matched!", HttpStatus.BAD_REQUEST);
        }
        customerService.register(customer);

        Map<String,String> responseBody =Collections.singletonMap("message","Your account created successfully, please check your email for activation.");
        logger.info("customer created successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    /*   Customer activation - verification*/
    @PutMapping(value="/confirm-account",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> confirmRegistration(@RequestParam("token") String token) {
        customerService.confirmRegisteredCustomer(token);
        Map<String,String> responseBody =Collections.singletonMap("message","Your account verified successfully");
        logger.info("your account verified successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
    @PostMapping(value="/resend-confirmation",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> resendLink(@RequestParam("email") String email){
        customerService.resendToken(email);
        Map<String,String> responseBody =Collections.singletonMap("message","Resend token link sent successfully");
        logger.info("Resend token link sent");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping("/customer/find")
    public Customer find(Principal user) {
        return customerService.find(user.getName());
    }

//    @GetMapping("/customer/find/all")
//    public Iterable<Customer> getCustomerDetails(@RequestParam("page") int page,@RequestParam("size") int size) {
//        return customerService.getCustomer(page,size);
//    }
//    public List<Customer> findAllCustomers() {
//        return customerService.findAllCustomer();
//    }

    @GetMapping("/customer/find/address")
    public List<Address> findingAddressOfCustomer(Principal user) {
        if(!customerService.checkIfUserExist(user.getName()))
            throw new EmailExistsException("Email not Exist Exception");
       return customerService.findAdress(user.getName());
    }

    @PutMapping(value="/customer/update",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateCustomer(Principal user,@Valid @RequestBody Customer customer, final HttpServletRequest request) {
        customerService.update(user.getName(), customer);
        Map<String,String> responseBody =Collections.singletonMap("message","Your account updated successfully");
        logger.info("your account updated successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PostMapping(value="/customer/add/address",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> newAdressOfCustomer(Principal user,@Valid @RequestBody Address address, final HttpServletRequest request) {
        customerService.newAddress(user.getName(), address);
        Map<String,String> responseBody =Collections.singletonMap("message","Your address added successfully");
        logger.info("your address added successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @DeleteMapping(value="/customer/delete/address",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteAdressOfCustomer(@Valid @RequestParam("id") Long id, Principal user, final HttpServletRequest request) {
        customerService.deleteAddress(id, user.getName());
        Map<String,String> responseBody =Collections.singletonMap("message","Your address deleted successfully");
        logger.info("your address deleted successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PutMapping(value="/customer/update/address",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateAdressOfCustomer(@Valid @RequestParam("id") Long id, Principal user,@RequestBody Address address, final HttpServletRequest request) {
        customerService.updateAddress(id, user.getName(), address);
        Map<String,String> responseBody =Collections.singletonMap("message","Your address updated successfully");
        logger.info("address updated successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PatchMapping(value="/customer/update/password",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updatePasswordOfCustomer(Principal user,@Valid @RequestBody UpdatePasswordDto updatePasswordDto, final HttpServletRequest request) {
        customerService.updatePassword(user.getName(), updatePasswordDto);
        Map<String,String> responseBody =Collections.singletonMap("message","Your password updated successfully");
     //   logger.in
        return new ResponseEntity<>(responseBody, HttpStatus.OK);

    }
    ////////////////////////////////////////////////////////////////////////

    @GetMapping("/customer/findall/category")
    public Iterable<Category> showAll(@RequestParam int page,@RequestParam int size){
        return customerService.showAll(page, size);
    }

    @GetMapping("/customer/get/categories")
    public MappingJacksonValue getCategories() {
        List<Category> list = customerService.getAllCategory();
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name");
        FilterProvider filters = new SimpleFilterProvider().addFilter("BeanFilter", filter);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(list);
        mappingJacksonValue.setFilters(filters);
        return mappingJacksonValue;
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    @GetMapping("/customer/viewproduct")
    public Optional<Product> findProduct(@RequestParam int id){
        return customerService.viewProduct(id);
    }

    @GetMapping("/customer/view/all/product")
    public Iterable<Product> findAllProduct(@RequestParam int categoryId){
        return customerService.viewAll(categoryId);
    }

    @GetMapping("/customer/view/all/SimilarProduct")
    public Iterable<Product> findAllSimilarProduct(@RequestParam("productId") int productId){
        return customerService.viewSimilarAll(productId);
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////

    @PostMapping(value="/customer/add/cart",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addToCart(@Valid @RequestBody CartDto cartDto, Principal user) {
        customerService.addCart(cartDto, user.getName());
        Map<String, String> responseBody = Collections.singletonMap("message", "Item added to cart successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @GetMapping("/customer/view/all/cart")
    public List<Cart> viewAllCart(Principal user){
        return customerService.showAllCart(user.getName());
    }

    @DeleteMapping(value = "/customer/delete/cart",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteCart(@RequestParam int variationId,Principal user){
        customerService.deletingCart(variationId, user.getName());
        Map<String, String> responseBody = Collections.singletonMap("message", "Item deleted from cart successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PutMapping(value = "/customer/update/cart",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateCart(@Valid @RequestBody CartDto cartDto,Principal user) {
        customerService.updateCart(cartDto,user.getName());
        Map<String, String> responseBody = Collections.singletonMap("message", "Item added to cart successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @DeleteMapping(value = "/customer/delete/all/cart",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteAllCart(Principal user){
        customerService.deletingAllCart(user.getName());
        Map<String, String> responseBody = Collections.singletonMap("message", "All Item deleted from cart successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @PostMapping(value = "/customer/order",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> customerOrder(@RequestParam long addressId, @Valid @RequestBody Order order, Principal user){
        customerService.customerOrder(addressId, order, user.getName());
        Map<String, String> responseBody = Collections.singletonMap("message", "Customer Order successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PostMapping(value = "/customer/partialOrder",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> customerPartialOrder(@RequestParam long addressId, @Valid @RequestBody PartialOrderDto partialOrderDto, Principal user){
        customerService.customerPartialOrder(addressId, partialOrderDto, user.getName());
        Map<String, String> responseBody = Collections.singletonMap("message", "Customer Order successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }


    @PostMapping(value = "/customer/directlyOrder",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> customerDirectlyOrder(@RequestParam long addressId, @RequestParam int productVariationId, @RequestParam int quantity,@Valid @RequestBody Order order, Principal user){
        customerService.customerDirectlyOrder(addressId,productVariationId, quantity,order, user.getName());
        Map<String, String> responseBody = Collections.singletonMap("message", "Customer Directly Order successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }


//    @PostMapping(value = "/customer/return",produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Object> customerReturnOrder(@RequestParam int orderProductId, Principal user){
//        customerService.customerreturningOrder(orderProductId, user.getName());
//        Map<String, String> responseBody = Collections.singletonMap("message", "Customer Directly Order successfully");
//        return new ResponseEntity<>(responseBody, HttpStatus.OK);
//    }
//
//    @PostMapping(value = "/customer/cancel",produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Object> customerCancelOrder(@RequestParam int orderProductId, Principal user){
//        customerService.customercancelingOrder(orderProductId, user.getName());
//        Map<String, String> responseBody = Collections.singletonMap("message", "Customer Directly Order successfully");
//        return new ResponseEntity<>(responseBody, HttpStatus.OK);
//    }

    @PutMapping(value = "/customer/cancelOrder",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> cancelOrder(@RequestParam int orderProductId, @RequestBody OrderStatus orderStatus, Principal user) {
        customerService.cancelOrder(orderProductId,orderStatus, user.getName());
        Map<String, String> responseBody = Collections.singletonMap("message", "Cancel order successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PutMapping(value = "/customer/returnOrder",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> returnOrder(@RequestParam int orderProductId, @RequestBody OrderStatus orderStatus, Principal user) {
        customerService.returnOrder(orderProductId,orderStatus,user.getName());
        Map<String, String> responseBody = Collections.singletonMap("message", "Return order successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @ApiOperation(value = "View a order")
    @GetMapping("/customer/viewOrder")
    public MappingJacksonValue findOrder(@RequestParam int id,Principal user){
       Order order = customerService.viewOrderById(id,user.getName());
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id","amount","dateCreated","paymentMethod","orderProductList");
        FilterProvider filters = new SimpleFilterProvider().addFilter("BeanFilter", filter);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(order);
        mappingJacksonValue.setFilters(filters);
        return mappingJacksonValue;
    }

    @GetMapping("/customer/view/all/order")
    public List<Order> findOrders(Principal user){
        return customerService.findAllOrders(user.getName());
    }
}

//    Category category = adminService.getCategory(id);
//    SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("name");
//    FilterProvider filters = new SimpleFilterProvider().addFilter("BeanFilter", filter);
//    MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(category);
//        mappingJacksonValue.setFilters(filters);



