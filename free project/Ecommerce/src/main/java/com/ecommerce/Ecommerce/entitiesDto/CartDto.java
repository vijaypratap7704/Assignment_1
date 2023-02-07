package com.ecommerce.Ecommerce.entitiesDto;

import javax.validation.constraints.Min;

public class CartDto {
    private int variationId;
    private int quantity;


    public int getVariationId() {
        return variationId;
    }

    public void setVariationId(int variationId) {
        this.variationId = variationId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
