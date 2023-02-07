package com.ecommerce.Ecommerce.entitiesDto;

import java.util.List;

public class PartialOrderDto {
    private String paymentMethod;
    private List<Integer> variationId;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<Integer> getVariationId() {
        return variationId;
    }

    public void setVariationId(List<Integer> variationId) {
        this.variationId = variationId;
    }
}
