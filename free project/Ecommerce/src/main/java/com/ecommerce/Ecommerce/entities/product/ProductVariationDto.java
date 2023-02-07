package com.ecommerce.Ecommerce.entities.product;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import java.util.Map;

@Data
public class ProductVariationDto {
    @Column(unique=true)
    private int id;
    private Map<String, String> metadata;
    @Min(0)
    private int quantityAvailable;
    //////////////
    private String primaryImageName;
    @Min(0)
    private int price;

}
