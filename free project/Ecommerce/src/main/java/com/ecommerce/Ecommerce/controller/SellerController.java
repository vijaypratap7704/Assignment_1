package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.entities.order.Order;
import com.ecommerce.Ecommerce.entities.order.OrderStatus;
import com.ecommerce.Ecommerce.entities.product.*;
import com.ecommerce.Ecommerce.entities.user.Address;
import com.ecommerce.Ecommerce.entities.user.Customer;
import com.ecommerce.Ecommerce.entities.user.Seller;
import com.ecommerce.Ecommerce.entities.user.User;
import com.ecommerce.Ecommerce.entitiesDto.UpdatePasswordDto;
import com.ecommerce.Ecommerce.service.SellerService;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
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
@RequestMapping(path = "/seller")
public class SellerController {

    Logger logger = Logger.getLogger(SellerController.class.toString());

    @Autowired
    private SellerService sellerService;

    @PostMapping(value = "/register",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> registerCustomer(@Valid @RequestBody Seller seller, final HttpServletRequest request) {
        if(!seller.getPassword().equals(seller.getConfirmPassword())){
            Map<String,String> responseBody = Collections.singletonMap("message","Password and confirm password not matched!");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }
        sellerService.register(seller);
        Map<String,String> responseBody =Collections.singletonMap("message","Your account created successfully, please check your email for activation.");
        logger.info("seller account created successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////
    @GetMapping("/find")
    public Seller findingCustomer(Principal user){
        return sellerService.find(user.getName());
    }

    @PutMapping(value="/update",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateSeller(Principal user,@Valid @RequestBody Seller seller, final HttpServletRequest request) {
        sellerService.update(user.getName(), seller);
        Map<String,String> responseBody = Collections.singletonMap("message","Your account updated successfully");
        logger.info("account updated successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PatchMapping(value="/update/password",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updatePasswordOfSeller(Principal user,@Valid @RequestBody UpdatePasswordDto updatePasswordDto, final HttpServletRequest request) {
        sellerService.updatePassword(user.getName(), updatePasswordDto);
        Map<String,String> responseBody = Collections.singletonMap("message","Your account updated successfully");
        logger.info("password updated successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PutMapping(value="/update/address",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateAdressOfCustomer(@RequestParam("id") Long id,Principal user,@Valid @RequestBody Address address, final HttpServletRequest request) {
        sellerService.updateAddress(id, user.getName(), address);
        Map<String,String> responseBody = Collections.singletonMap("message","Your address added successfully");
        logger.info("address updated successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @GetMapping("/find/category")
     public Iterable<Category> find(@RequestParam int page,@RequestParam int size){
        return sellerService.showAll(page,size);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @PostMapping(value="/addproduct",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addNewProduct(@Valid @RequestBody ProductDto productDto, Principal user) {
        sellerService.addProduct(productDto, user.getName());
        Map<String,String> responseBody = Collections.singletonMap("message","Link to activate the product has been sent.");
        logger.info("product activation link sent");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @GetMapping("/viewproduct")
    public Optional<Product> findProduct(@RequestParam int id){
        return sellerService.viewProduct(id);
    }

    @GetMapping("/view/all/product")
    public Iterable<Product> findAllProduct(@RequestParam int page,@RequestParam int size){
        return sellerService.viewAll(page, size);
    }

    @DeleteMapping(value="/delete/product",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteProduct(@RequestParam("id") int id){
        sellerService.deleteProductById(id);
        Map<String,String> responseBody = Collections.singletonMap("message","Your product deleted successfully");
        logger.info("product deleted successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
    @PutMapping(value="/update/product",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateProduct(@Valid @RequestBody ProductDto productDto,@RequestParam int id) {
        sellerService.updatingProduct(productDto,id);
        Map<String,String> responseBody = Collections.singletonMap("message","product updated successfully");
        logger.info("product updated successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @PostMapping(value="/add/variationproduct",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addNewVariationProduct(@Valid @RequestBody ProductVariationDto productVariationDto) {
        sellerService.addVariationProduct(productVariationDto);
        Map<String,String> responseBody = Collections.singletonMap("message","product variation is added.");
        logger.info("variation product is added");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @GetMapping("/view/variationproduct")
    public Optional<ProductVariation> findvariatoinProduct(@RequestParam int id){
        return sellerService.viewVariationProduct(id);
    }

    @GetMapping("/view/all/variationproduct")
    public Iterable<ProductVariation> findAllvariationProduct(@RequestParam int page,@RequestParam int size){
        return sellerService.viewAllvariationProduct(page,size);
    }

    @PutMapping(value="/update/variationproduct",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateVariationProduct(@Valid @RequestBody ProductVariationDto productVariationDto,@RequestParam int id) {
        sellerService.updatingVariationProduct(productVariationDto,id);
        Map<String,String> responseBody = Collections.singletonMap("message","product variation updated successfully");
        logger.info("variation product is updated successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//    @GetMapping("/customer/view/all/order")
//    public List<Order> findOrders(Principal user){
//        return sellerService.findAllOrders(user.getName());
//    }

    @PutMapping(value = "/cancelOrder",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> cancelOrder(@RequestParam int orderProductId, @RequestBody OrderStatus orderStatus, Principal user) {
        sellerService.cancelOrder(orderProductId,orderStatus, user.getName());
        Map<String, String> responseBody = Collections.singletonMap("message", "Cancel order successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

}

