package com.ecommerce.Ecommerce.entitiesDto;

import javax.validation.constraints.Pattern;

public class UpdatePasswordDto {

    @Pattern(regexp="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message="Password in this format")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
