package com.ecommerce.Ecommerce.entitiesDto;

import com.ecommerce.Ecommerce.entities.product.CategoryMetadataFieldValues;
import com.ecommerce.Ecommerce.entities.product.Product;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class CategoryDto {
    @NotEmpty
    private String name;
    private int parentCategoryId;
    private List<CategoryMetadataFieldValues> categoryMetadataFieldValuesList;
    private List<Product> productList;

    public List<CategoryMetadataFieldValues> getCategoryMetadataFieldValuesList() {
        return categoryMetadataFieldValuesList;
    }

    public void setCategoryMetadataFieldValuesList(List<CategoryMetadataFieldValues> categoryMetadataFieldValuesList) {
        this.categoryMetadataFieldValuesList = categoryMetadataFieldValuesList;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(int parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }
}
