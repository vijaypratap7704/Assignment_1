package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.Exception.CategoryIdException;
import com.ecommerce.Ecommerce.entities.order.Order;
import com.ecommerce.Ecommerce.entities.order.OrderStatus;
import com.ecommerce.Ecommerce.entities.product.Category;
import com.ecommerce.Ecommerce.entities.product.CategoryMetadataField;
import com.ecommerce.Ecommerce.entities.product.Product;
import com.ecommerce.Ecommerce.entities.user.Customer;
import com.ecommerce.Ecommerce.entities.user.Seller;
import com.ecommerce.Ecommerce.entitiesDto.CategoryDto;
import com.ecommerce.Ecommerce.entitiesDto.CategoryMetaDataFieldValuesDto;
import com.ecommerce.Ecommerce.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;


@RestController
//@RequestMapping(name = "/admin")
public class AdminController {
    Logger logger = LoggerFactory.getLogger(AdminController.class.toString());

    @Autowired
    private AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/customers")
    public Iterable<Customer> getCustomerDetails(@RequestParam("page") int page,@RequestParam("size") int size) {
        return adminService.getCustomer(page,size);
    }

    @GetMapping("/sellers")
    public Iterable<Seller> getSellerDetails(@RequestParam("page") int page,@RequestParam("size") int size) {
        return adminService.getSeller(page,size);
    }

    @PutMapping("/activatecustomer/{id}")
    public ResponseEntity<String> activateCustomer(@PathVariable int id){
        logger.info("customer account is activated");
        return adminService.activateCustomer(id);
    }

    @PutMapping("/deactivatecustomer/{id}")
    public ResponseEntity<String> deActivateCustomer(@PathVariable int id){
        logger.info("customer account is deactivated");
        return adminService.deActivateCustomer(id);
    }

    @PutMapping("/activateseller/{id}")
    public ResponseEntity<String> activateSeller(@PathVariable int id){
        logger.info("seller account is activated");
        return adminService.activateSeller(id);
    }

    @PutMapping("/deactivateseller/{id}")
    public ResponseEntity<String> deActivateSeller(@Valid @PathVariable int id){
        logger.info("seller account is deactivated");
        return adminService.deActivateSeller(id);
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @PostMapping("/metadatafield/add")
    public ResponseEntity<String> addMetaField(@Valid @RequestBody CategoryMetadataField categoryMetadataField){
        logger.info("metadata field is added");
        return adminService.addField(categoryMetadataField);
    }

    @GetMapping("/metadatafield/find")
    public Iterable<CategoryMetadataField> findMetaField(@RequestParam int page,@RequestParam int size){
        return adminService.findField(page,size);
    }

    @PostMapping("/category/add")
    public ResponseEntity<String> addCategory(@Valid @RequestBody CategoryDto categoryDto){
        logger.info("category field is added");
        return adminService.addCategoryField(categoryDto);
    }

    @GetMapping("/category/find")
    public Optional<Category> findCategory(@RequestParam("id") int id){
        Optional<Category> category = adminService.findCategory(id);
        if(!category.isPresent())
            throw new CategoryIdException("Category id not found");
        return adminService.findCategory(id);
    }
    @GetMapping("/category/findall")
    public Iterable<Category> findCategories(@RequestParam int page,@RequestParam int size){
        return adminService.findCategories(page,size);
    }

    @PutMapping("/category/update")
    public ResponseEntity<String> update(@RequestParam int id,@Valid @RequestBody CategoryDto categoryDto){
        logger.info("category field is updated");
        return adminService.updateCategory(id,categoryDto);
    }

    @PostMapping("/category/add/metadatafieldvalues")
    public ResponseEntity<String> addMeta(@Valid @RequestBody CategoryMetaDataFieldValuesDto categoryMetaDataFieldValuesDto){
        logger.info("metadatafieldvalue is added ");
        return adminService.addMetadataField(categoryMetaDataFieldValuesDto);
    }

    @PutMapping("/category/update/metadatafieldvalues")
    public ResponseEntity<String> updateMeta(@Valid @RequestBody CategoryMetaDataFieldValuesDto categoryMetaDataFieldValuesDto){
        logger.info("metadatafieldvalue is updated");
        return adminService.updateMetadataField(categoryMetaDataFieldValuesDto);
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @PutMapping(value ="/admin/activate/product",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> productActivation(@RequestParam("id") int id) {
        adminService.productActivate(id);
        Map<String,String> responseBody = Collections.singletonMap("message","Your product activated successfully");
        logger.info("product is activated");
       // logger.error("");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PutMapping(value="/admin/Deactivate/product",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> productDeactivation(@RequestParam("Id") int id) {
        adminService.productDeactivate(id);
        Map<String,String> responseBody = Collections.singletonMap("message","Your product Deactivated successfully");
        logger.info("product is deactivated");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @GetMapping(value = "/admin/viewproduct")
    public Optional<Product> findProduct(@RequestParam int id){
        return adminService.viewProduct(id);
    }

    @GetMapping("/admin/view/all/product")
    public Iterable<Product> findAllProduct(@RequestParam int page,@RequestParam int size){
        return adminService.viewAll(page,size);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @PutMapping(value = "/cancelOrder",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> cancelOrder(@RequestParam int orderProductId, @RequestBody OrderStatus orderStatus, Principal user) {
        adminService.cancelOrder(orderProductId,orderStatus, user.getName());
        Map<String, String> responseBody = Collections.singletonMap("message", "Cancel order successfully");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
    @GetMapping("/admin/view/all/order")
    public Iterable<Order> findAllOrder(@RequestParam int page, @RequestParam int size){
        return adminService.viewAllOrder(page,size);
    }

}
