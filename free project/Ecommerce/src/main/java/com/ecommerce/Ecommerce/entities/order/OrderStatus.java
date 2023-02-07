package com.ecommerce.Ecommerce.entities.order;


import com.ecommerce.Ecommerce.entities.product.ProductVariation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

//@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class OrderStatus {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
    String transitionNotesComments;
    private Status fromStatus;
    private Status toStatus;

//    @OneToOne
//    @JoinColumn(name = "order_product_id", referencedColumnName = "id")
//    private ProductVariation productVariation;


    enum orderStatus {PLACED, CANCELLED, DELIVERED, RETURNED}

    enum fromStatus {
        ORDER_PLACED, CANCELLED, ORDER_REJECTED, ORDER_CONFIRMED,
        ORDER_SHIPPED, DELIVERED, RETURN_REQUESTED, RETURN_REJECTED,
        RETURN_APPROVED, PICK_UP_INITIATED, PICK_UP_COMPLETED,
        REFUND_INITIATED, REFUND_COMPLETED
    }

    enum toStatus {
        CANCELLED, ORDER_CONFIRMED, ORDER_REJECTED, ORDER_SHIPPED,
        REFUND_INITIATED, DELIVERED, CLOSED, RETURN_REQUESTED, RETURN_APPROVED,
        PICK_UP_INITIATED, PICK_UP_COMPLETED, RETURN_REJECTED, REFUND_COMPLETED
    }



}

