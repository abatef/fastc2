package com.abatef.fastc2.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class FirebaseUserException extends RuntimeException {
    public FirebaseUserException(String username) {
        super("The User is a Firebase User: " + username);
    }
}
