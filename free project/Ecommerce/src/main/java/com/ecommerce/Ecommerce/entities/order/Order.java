package com.ecommerce.Ecommerce.entities.order;


import com.ecommerce.Ecommerce.entities.user.Customer;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@JsonFilter("BeanFilter")
@EntityListeners(AuditingEntityListener.class)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int amount;
    @CreatedDate
    private Date dateCreated;
    private String paymentMethod;

    private String customerAddressAddressLine;
    private String customerAddressCity;
    private String customerAddressState;
    private String customerAddressCountry;
    private int customerAddressZipcode;
    private String customerAddressLabel;

    //@JsonIgnore
    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    //@JsonIgnore
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProductList;

}
