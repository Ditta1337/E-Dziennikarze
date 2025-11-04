package com.edziennikarze.gradebook.exception;

public class ConfigurationAlreadyCalculatedException extends RuntimeException {
    public ConfigurationAlreadyCalculatedException(String message) {
        super(message);
    }
}
