package com.ecommerce.Ecommerce.Exception;

public class AlreadyActiveAccountException extends RuntimeException{
    public AlreadyActiveAccountException(String message) {
        super(message);
    }
}
