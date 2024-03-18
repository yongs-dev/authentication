package com.mark.authentication.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(final String email) {
        super(email + " NotFoundException");
    }
}
