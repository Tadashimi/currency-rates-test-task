package com.ukolpakova.soap.exception;

/**
 * Custom exception for missing entities.
 */
public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(String message) {
        super(message);
    }
}
