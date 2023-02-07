package com.ecommerce.Ecommerce.service;


import com.ecommerce.Ecommerce.Exception.*;
import com.ecommerce.Ecommerce.entities.order.*;
import com.ecommerce.Ecommerce.entities.product.Category;
import com.ecommerce.Ecommerce.entities.product.Product;
import com.ecommerce.Ecommerce.entities.product.ProductVariation;
import com.ecommerce.Ecommerce.entities.user.*;
import com.ecommerce.Ecommerce.entitiesDto.CartDto;
import com.ecommerce.Ecommerce.entitiesDto.PartialOrderDto;
import com.ecommerce.Ecommerce.entitiesDto.UpdatePasswordDto;
import com.ecommerce.Ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;

@Service
public class CustomerService {

    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Autowired
    private UserService userService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariationRepository productVariationRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;


    public boolean checkIfUserExist(String email) {
        return customerRepository.findByEmail(email) != null;
    }


    public void register(@Valid Customer customer) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (checkIfUserExist(customer.getEmail())) {
            throw new EmailExistsException("Email already registered");
        }
        if (customer.getEmail().equals("")) {
            throw new EmailNotNullException("Email Should not be null");
        }
        Role role = new Role();
        role.setAuthority("ROLE_CUSTOMER");
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setRoleList(Arrays.asList(role));
        List<Address> addressList = customer.getAddressList();
        for (Address address : addressList) {
            address.setCustomer(customer);
        }
        customerRepository.save(customer);
        userService.sendActivationLinkCustomer(customer);
    }

    public void confirmRegisteredCustomer(String token) {
        String result = userService.validateVerificationTokenAndSaveUser(token);
        if (result.equals("invalidToken")) {
            throw new InvalidTokenException("Invalid Token");
        }
        if (result.equals("expired")) {
            TokenConfirm tokenConfirm = tokenRepository.findByToken(token);
            User customer = tokenConfirm.getUser();
            String email = customer.getEmail();
            resendToken(email);
            throw new InvalidTokenException("Token expired");
        }
    }

    public void resendToken(String email) {
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            throw new UserNotFoundException("No such email found!");
        }
        if (customer.isActive()) {
            userService.sendResetPasswordMessage(customer);
        } else {
            TokenConfirm oldToken = tokenRepository.findTokenByUserId(customer.getId());
            tokenRepository.delete(oldToken);
            userService.sendActivationLinkCustomer(customer);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Customer find(String email) {
        Customer customer = customerRepository.findByEmail(email);
        return customer;
    }
//    public List<Customer> findAllCustomer(){
//        return customerRepository.findAll();
//    }

//    public Iterable<Customer> getCustomer(int page,int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
//        Page<Customer> allCustomers = customerRepository.findAll(pageable);
//        return allCustomers;
//    }

    public List<Address> findAdress(String email) {
        Customer customer = customerRepository.findByEmail(email);
        return customer.getAddressList();
    }


    public void update(String email, Customer customer) {
        if (checkIfUserExist(email)) {
            Customer customer1 = customerRepository.findByEmail(email);
            customer1.setContact(customer.getContact());
            customer1.setFirstName(customer.getFirstName());
            customer1.setMiddleName(customer.getMiddleName());
            customer1.setLastName(customer.getLastName());
            customerRepository.save(customer1);
        } else {
            throw new UserNotFoundException("Email not valid");
        }
    }

    public void newAddress(String email, Address address) {
        if (checkIfUserExist(email)) {
            Customer customer = customerRepository.findByEmail(email);
            List<Address> addressList = customer.getAddressList();
            addressList.add(address);
            customer.setAddressList(addressList);
            address.setCustomer(customer);
            customerRepository.save(customer);
        } else {
            throw new UserNotFoundException("Email not valid");
        }
    }


    public void deleteAddress(long id, String email) {
        Customer customer = customerRepository.findByEmail(email);
        List<Address> addressList = customer.getAddressList();
        Iterator<Address> iterator = addressList.iterator();
        if (!addressRepository.findById(id).isPresent())
            throw new UserNotFoundException("Address id is not present");
        else {
            while (iterator.hasNext()) {
                Address address = iterator.next();
                if (address.getId() == id) {
                    iterator.remove();
                    customer.setAddressList(addressList);
                    addressRepository.delete(address);
                    break;
                }
            }
        }

//        customer.setAddressList(addressList);
//        customerRepository.save(customer);
    }

    public void updateAddress(long id, String email, Address address) {
        if (checkIfUserExist(email)) {
            Customer customer = customerRepository.findByEmail(email);
            List<Address> addressList = customer.getAddressList();
            Iterator<Address> iterator = addressList.iterator();
            if (!addressRepository.findById(id).isPresent())
                throw new UserNotFoundException("Address id is not present");
            else {
                while (iterator.hasNext()) {
                    Address address1 = iterator.next();
                    if (address1.getId() == id) {
                        address1.setAddressLine(address.getAddressLine());
                        address1.setCity(address.getCity());
                        address1.setCountry(address.getCountry());
                        address1.setLabel(address.getLabel());
                        address1.setState(address.getState());
                        address1.setZipCode(address.getZipCode());
                    }
                }
                customer.setAddressList(addressList);
                customerRepository.save(customer);
            }
        } else {
            throw new UserNotFoundException("Email not valid");
        }
    }

    public void updatePassword(String email, UpdatePasswordDto updatePasswordDto) {
        if (checkIfUserExist(email)) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            Customer customer1 = customerRepository.findByEmail(email);
            customer1.setPassword(passwordEncoder.encode(updatePasswordDto.getPassword()));
            customerRepository.save(customer1);
        } else {
            throw new UserNotFoundException("Email not valid");
        }
    }


    public Iterable<Category> showAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        return categoryRepository.findAll(pageable);

    }

    public List<Category> getAllCategory() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<Category> allCategories = categoryRepository.findAll(pageable);
        if (allCategories.hasContent()) {
            return allCategories.getContent();
        } else {
            return new ArrayList<Category>();
        }
    }

    public Optional<Product> viewProduct(int productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (!(product.isPresent()))
            throw new ProductIdException("product Id not found.");
        return product;
    }

    public Iterable<Product> viewAll(int id) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
//        return productRepository.findAll(pageable);
        //    Optional<Category> category = categoryRepository.findById(id);
        List<Product> product = productRepository.findByCategory(id);
        return product;

    }

    public Iterable<Product> viewSimilarAll(int productId) {
        Optional<Product> product = productRepository.findById(productId);
        //   Optional<Category> category = categoryRepository.findById(product.get().getCategory().getId());
        List<Product> product1 = productRepository.findByCategory(product.get().getCategory().getId());
        return product1;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////

    public void addCart(CartDto cartDto, String email) {
        Optional<ProductVariation> productVariation = productVariationRepository.findById(cartDto.getVariationId());
        Customer customer = customerRepository.findByEmail(email);
        if (productVariation.isPresent()) {
            ProductVariation productVariation1 = productVariation.get();
            Optional<Cart> cart1 = cartRepository.findByProductVariationId(cartDto.getVariationId());
            if (productVariation1.getProduct().isActive() == false)
                throw new CategoryIdException("product is not available");
            else if ((productVariation1.getQuantityAvailable() < 1) && (productVariation1.getIsActive() == false))
                throw new CategoryIdException("Right now Product Variation is not Available");
            else if (cart1.isPresent()) {
                Cart cart = cart1.get();
                cart.setQuantity(cartDto.getQuantity() + cart.getQuantity());
                cartRepository.save(cart);
            } else {
                Cart cart = new Cart();
                cart.setCustomer(customer);
                cart.setQuantity(cartDto.getQuantity());
                cart.setProductVariation(productVariation1);
                //     productVariation1.setQuantityAvailable(productVariation1.getQuantityAvailable()-cartDto.getQuantity());
                //     productVariationRepository.save(productVariation1);
                cartRepository.save(cart);
            }
        } else {
            throw new CategoryIdException("That Product Variation is not Available");
        }
    }

    public List<Cart> showAllCart(String email) {
        //    Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        Customer customer = customerRepository.findByEmail(email);
        return cartRepository.findByCustomerId(customer.getId());

    }

    @Transactional
    public void deletingCart(int variationId, String email) {
        Optional<ProductVariation> productVariation = productVariationRepository.findById(variationId);
        if (!productVariation.isPresent()) {
            throw new CategoryIdException("No such product exist");
        } else {
//            ProductVariation productVariation1 = productVariation.get();
            Customer customer = customerRepository.findByEmail(email);
//            Cart cart = cartRepository.findByProductVariationId(variationId);
//            System.out.println(cart);
            cartRepository.DeleteCart(customer.getId(), variationId);
        }
    }

    public void updateCart(CartDto cartDto, String name) {
        Customer customer = customerRepository.findByEmail(name);
        Optional<Cart> cart = cartRepository.findByProductVariationId(cartDto.getVariationId());
        if (!cart.isPresent())
            throw new CategoryIdException("No product is available");
        else {
            Cart cart1 = cart.get();
            if (cartDto.getQuantity() <= 0) {
                cartRepository.delete(cart1);
            } else {
                cart1.setQuantity(cartDto.getQuantity());
                cartRepository.save(cart1);
            }
        }
    }

    public void deletingAllCart(String email) {
        Customer customer = customerRepository.findByEmail(email);
        List<Cart> cart = cartRepository.findByCustomerId(customer.getId());
        cartRepository.deleteInBatch(cart);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void customerOrder(long addressId, Order order, String email) {
        Optional<Address> address = addressRepository.findById(addressId);

        Customer customer = customerRepository.findByEmail(email);

        List<Cart> carts = cartRepository.findByCustomerId(customer.getId());
        Order order1 = new Order();
        int totalAmount = 0;

        List<OrderProduct> orderProductList = new ArrayList<>();
        for (Cart cart : carts) {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setQuantity(cart.getQuantity());
            orderProduct.setPrice((cart.getProductVariation().getPrice() * cart.getQuantity()));
            orderProduct.setProductVariationMetadata(cart.getProductVariation().getMetadata());
            orderProduct.setProductVariation(cart.getProductVariation());
            OrderStatus status = new OrderStatus();
            status.setFromStatus(Status.ORDER_PLACED);
            status.setToStatus(Status.ORDER_CONFIRMED);
            orderProduct.setOrderStatus(status);
            orderProductList.add(orderProduct);
            totalAmount = totalAmount + orderProduct.getPrice();
        }

        order1.setCustomerAddressAddressLine(address.get().getAddressLine());
        order1.setCustomerAddressCity(address.get().getCity());
        order1.setCustomerAddressCountry(address.get().getCountry());
        order1.setCustomerAddressLabel(address.get().getLabel());
        order1.setCustomerAddressState(address.get().getState());
        order1.setCustomerAddressZipcode(address.get().getZipCode());
//order.setAmount(order.getAmount());
        order1.setPaymentMethod(order.getPaymentMethod());
        order1.setAmount(totalAmount);

        order1.setOrderProductList(orderProductList);
//Customer customer = customerRepository.findByEmail(email);
//order.setCustomer(customer);
        customer.addOrder(order1);
        customerRepository.save(customer);
        List<Cart> cart = cartRepository.findByCustomerId(customer.getId());
        cartRepository.deleteInBatch(cart);
//orderRepository.save(order);

    }

    @Transactional
    public void customerPartialOrder(long addressId, PartialOrderDto partialOrderDto, String email) {
        Optional<Address> address = addressRepository.findById(addressId);
        if (!address.isPresent())
            throw new CategoryIdException("Address Id not present");
        Customer customer = customerRepository.findByEmail(email);
        List<Cart> carts = cartRepository.findByCustomerId(customer.getId());
        Order order1 = new Order();
        int totalamount = 0;
        List<OrderProduct> orderProductList = new ArrayList<>();
        for (Cart cart : carts) {
            if(partialOrderDto.getVariationId().contains(cart.getProductVariation().getId())) {
                OrderProduct orderProduct1 = new OrderProduct();
                orderProduct1.setQuantity(cart.getQuantity());
                orderProduct1.setPrice((cart.getProductVariation().getPrice() * cart.getQuantity()));
                orderProduct1.setProductVariationMetadata(cart.getProductVariation().getMetadata());
                orderProduct1.setProductVariation(cart.getProductVariation());
                orderProduct1.setOrder(order1);
                OrderStatus status = new OrderStatus();
                status.setFromStatus(Status.ORDER_PLACED);
                status.setToStatus(Status.ORDER_CONFIRMED);
                orderProduct1.setOrderStatus(status);
                orderProductList.add(orderProduct1);
                totalamount = totalamount + orderProduct1.getPrice();
            }
        }

        order1.setCustomerAddressAddressLine(address.get().getAddressLine());
        order1.setCustomerAddressCity(address.get().getCity());
        order1.setCustomerAddressCountry(address.get().getCountry());
        order1.setCustomerAddressLabel(address.get().getLabel());
        order1.setCustomerAddressState(address.get().getState());
        order1.setCustomerAddressZipcode(address.get().getZipCode());
        order1.setPaymentMethod(partialOrderDto.getPaymentMethod());
        order1.setAmount(totalamount);
        order1.setDateCreated(new Date());

        order1.setOrderProductList(orderProductList);
        customer.addOrder(order1);
        customerRepository.save(customer);
        List<Integer> list = partialOrderDto.getVariationId();
        for(Integer id:list) {
            cartRepository.DeleteCart(customer.getId(),id);
        }
    }

    public void customerDirectlyOrder(long addressId, int productVariationId, int quantity, Order order, String email) {
        Optional<Address> address = addressRepository.findById(addressId);
        if (!address.isPresent())
            throw new CategoryIdException("Address Id not present");

        Optional<ProductVariation> productVariation = productVariationRepository.findById(productVariationId);
        if (!productVariation.isPresent())
            throw new ProductIdException("Product Variation Id not present");

        ProductVariation productVariation1 = productVariation.get();
        productVariation1.setQuantityAvailable(productVariation1.getQuantityAvailable() - quantity);


        List<OrderProduct> orderProductList = new ArrayList<>(); //////
        int totalamount = 0;

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setQuantity(quantity);
        orderProduct.setPrice((int) productVariation1.getPrice() * quantity);
        orderProduct.setProductVariationMetadata(productVariation1.getMetadata());
        orderProduct.setProductVariation(productVariation1);
        orderProduct.setOrder(order);
        OrderStatus status = new OrderStatus();
        status.setFromStatus(Status.ORDER_PLACED);
        status.setToStatus(Status.ORDER_CONFIRMED);
        orderProduct.setOrderStatus(status);
        totalamount = totalamount + orderProduct.getPrice();

        orderProductList.add(orderProduct); //////
//orderProductRepository.save(orderProduct);

        order.setCustomerAddressAddressLine(address.get().getAddressLine());
        order.setCustomerAddressCity(address.get().getCity());
        order.setCustomerAddressCountry(address.get().getCountry());
        order.setCustomerAddressLabel(address.get().getLabel());
        order.setCustomerAddressState(address.get().getState());
        order.setCustomerAddressZipcode(address.get().getZipCode());

        order.setAmount(totalamount);
        order.setPaymentMethod(order.getPaymentMethod());
        order.setDateCreated(new Date());
        order.setOrderProductList(orderProductList);//////////
//productVariationRepository.save(productVariation1);

        Customer customer = customerRepository.findByEmail(email);
        order.setCustomer(customer);
        customer.addOrder(order);
        customerRepository.save(customer);

    }

//    public void customerreturningOrder(int orderProductId, String name) {
//        Optional<OrderProduct> orderProduct  = orderProductRepository.findById(orderProductId);
//        if(orderProduct.isPresent()){
//            OrderProduct orderProduct1 = orderProduct.get();
//            OrderStatus orderStatus = orderProduct1.getOrderStatus();
//            orderStatus.setToStatus(Status.RETURN_APPROVED);
//            orderStatus.setFromStatus(Status.PICK_UP_INITIATED);
//        }
//        else
//            throw new CategoryIdException("order product is not present");
//    }
//
//    public void customercancelingOrder(int orderProductId, String name) {
//        Optional<OrderProduct> orderProduct  = orderProductRepository.findById(orderProductId);
//        if(orderProduct.isPresent()){
//            OrderProduct orderProduct1 = orderProduct.get();
//            OrderStatus orderStatus = orderProduct1.getOrderStatus();
//            orderStatus.setToStatus(Status.CANCELLED);
//            orderStatus.setFromStatus(Status.ORDER_REJECTED);
//        }
//        else
//            throw new CategoryIdException("order product is not present");
//    }

    public void cancelOrder(int orderProductId,OrderStatus orderStatus, String email){
        Optional<OrderProduct> orderProduct1 = orderProductRepository.findById(orderProductId);
        if(!orderProduct1.isPresent())
            throw new CategoryIdException("Order Product Id not present");

        OrderStatus status = new OrderStatus();
            status.setFromStatus(Status.ORDER_PLACED);
            status.setToStatus(Status.CANCELLED);
//status.setFromStatus(orderStatus.getFromStatus());
//status.setToStatus(orderStatus.getToStatus());
//status.setTransitionNotesComments(orderStatus.getTransitionNotesComments());
            orderProduct1.get().setOrderStatus(status);
            orderProductRepository.save(orderProduct1.get());
    }

    public void returnOrder(int orderProductId, OrderStatus orderStatus, String email){
        Optional<OrderProduct> orderProduct1 = orderProductRepository.findById(orderProductId);
        if(!orderProduct1.isPresent())
            throw new CategoryIdException("Order Product Id not present");
        OrderStatus status = new OrderStatus();
            status.setFromStatus(Status.DELIVERED);
            status.setToStatus(Status.RETURN_REQUESTED);
//status.setFromStatus(orderStatus.getFromStatus());
//status.setToStatus(orderStatus.getToStatus());
//status.setTransitionNotesComments(orderStatus.getTransitionNotesComments());
            orderProduct1.get().setOrderStatus(status);
            orderProductRepository.save(orderProduct1.get());
//        else
//            throw new CategoryIdException("Your Order not return");
    }

    public Order viewOrderById(int id,String email) {
        Customer customer = customerRepository.findByEmail(email);
        Optional<Order> order = orderRepository.findById(id);
        if(order.isPresent()) {
            Order order1 = order.get();
            if(order1.getCustomer() == customer)
                return order1;
            else
                throw new CategoryIdException("user cannot order this product");
        }
        else
            throw new CategoryIdException("No such order is present");
    }

    public List<Order> findAllOrders(String email) {
        Customer customer = customerRepository.findByEmail(email);
        return orderRepository.findByCustomerId(customer.getId());
    }
}
