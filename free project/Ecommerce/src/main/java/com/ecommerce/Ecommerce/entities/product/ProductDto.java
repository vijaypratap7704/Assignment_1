package com.ecommerce.Ecommerce.entities.product;

import com.ecommerce.Ecommerce.entities.user.Seller;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ProductDto {
    @NotEmpty
    private String name;

    private String description;

    private Boolean isCancellable;

    private Boolean isReturnable;

    @NotEmpty
    private String brand;

    @NotNull
    private int categoryId;

    private int sellerId;

    public ProductDto(@NotNull String name, String description, Boolean isCancellable, Boolean isReturnable, @NotNull String brand, @NotNull int categoryId, int sellerId) {
        this.name = name;
        this.description = description;
        this.isCancellable = isCancellable;
        this.isReturnable = isReturnable;
        this.brand = brand;
        this.categoryId = categoryId;
        this.sellerId = sellerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Boolean getCancellable() {
        return isCancellable;
    }

    public void setCancellable(Boolean cancellable) {
        isCancellable = cancellable;
    }

    public Boolean getReturnable() {
        return isReturnable;
    }

    public void setReturnable(Boolean returnable) {
        isReturnable = returnable;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }
}


//{
//        "name":"Polo TShirt",
//        "description":"TShirt for Men",
//        "isCancellable":false,
//        "isReturnable":false,
//        "brand":"Nike",
//        "isActive":false,
//        "categoryId":1,
//        "sellerId":3
//        }