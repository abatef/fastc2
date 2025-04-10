package com.abatef.fastc2.exceptions;

public class FirebaseUserException extends RuntimeException {
    public FirebaseUserException(String username) {
        super("The User is a Firebase User: " + username);
    }
}
