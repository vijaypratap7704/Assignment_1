package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.entities.product.Category;
import com.ecommerce.Ecommerce.entities.product.Product;
import com.ecommerce.Ecommerce.entities.user.Seller;
import com.ecommerce.Ecommerce.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Integer> {
   // Product findById(int id);

   // Product findByNameAndSellerAndBrandAndCategory(String name, Seller seller, String brand, Category category);
//   @Query(value = "from Product p where p.category.id=:id and p.active=true")
//    List<Product> findAll(@RequestParam int id);


//    @Query(value = "from Product p where p.category.id=:id and p.active=true")
//    List<Product> fetchSimilarProducts(@Param("id") Long id,Pageable sortById);
     //   @Query(value = "Select * from Product p where p.category.id=:category.id and p.isActive=true")
        @Query("from Product where isActive = true")
        List<Product> findByCategory(Category category);

        List<Product> findAllByCategory(Category category);

//        @Query("from Product where isActive = true")
//        Optional<Product> findById(int id);

        @Query(value = "from Product p where p.category.id=:id and p.isActive=true")
        List<Product> findByCategory(int id);

//        @Query(value = "from Product p where p.id=:id and p.isActive=true")
//        Optional<Product> findById(int id);
}
////////////////////////////////////////////////////////////////////////////
//public CustomerFilterCategoryDTO filterCategory(Long id) {
//
//        Category category = categoryRepository.findById(id).orElse(null);
//        if (category == null){
//                throw new EcommerceException(ErrorCode.CATEGORY_NOT_FOUND);
//        }
//
//        Double maxPrice = productVariationRepository.getMaxPrice(id);
//        Double minPrice = productVariationRepository.getMinPrice(id);
//
//        List<CategoryMetadataFieldValue> categoryMetadataFieldValueList = metadataFieldValuesRepository.findByCategoryId(id);
//        if (categoryMetadataFieldValueList.size()==0){
//                throw new EcommerceException(ErrorCode.FIELD_VALUE_NOT_FOUND);
//        }
//
//        List<CategoryMetadataFieldResponseDTO> metadataFieldResponseDTOList = new ArrayList<>();
//
//        for (CategoryMetadataFieldValue fieldResponseDTO : categoryMetadataFieldValueList){
//
//                CategoryMetadataFieldResponseDTO fieldResponse = new CategoryMetadataFieldResponseDTO();
//                fieldResponse.setName(fieldResponseDTO.getMetadataField().getName());
//                fieldResponse.setFieldValues(Arrays.asList(fieldResponseDTO.getMetadataValue()));
//                metadataFieldResponseDTOList.add(fieldResponse);
//
//        }
//
//        List<String> brandList = productRepository.fetchBrandList(category.getId());
//
//        CustomerFilterCategoryDTO customerFilterCategoryDTO = new CustomerFilterCategoryDTO();
//        customerFilterCategoryDTO.setCategoryId(category.getId());
//        customerFilterCategoryDTO.setCategoryName(category.getName());
//        customerFilterCategoryDTO.setBrand(brandList);
//        customerFilterCategoryDTO.setMaxPrice(maxPrice);
//        customerFilterCategoryDTO.setMinPrice(minPrice);
//        customerFilterCategoryDTO.setMetadataFieldResponseDTOList(metadataFieldResponseDTOList);
//
//
//        return customerFilterCategoryDTO;
//}
