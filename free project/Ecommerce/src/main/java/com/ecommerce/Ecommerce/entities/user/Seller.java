package com.ecommerce.Ecommerce.entities.user;

import com.ecommerce.Ecommerce.entities.product.Product;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seller extends User {

    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",message="gst must be valid")
    private String gst;

    @Pattern(regexp = "^[1-9]{1}[0-9]{9}$",message = "contact must be of 10 digit")
    String contact;

    @CreatedDate
    private Date creationTime;

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(Date modificationTime) {
        this.modificationTime = modificationTime;
    }

    @LastModifiedDate
    private Date modificationTime;

    @Column(unique = true)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private String companyName;

    @OneToOne(mappedBy = "seller", cascade = CascadeType.ALL)
    private Address address;

    @OneToMany(mappedBy = "seller", cascade =CascadeType.ALL)
    private List<Product> productList;

}
