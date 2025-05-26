package com.example.demo;

class EvenementDejaExistantException extends Exception {
    public EvenementDejaExistantException(String message) {
        super(message);
    }
}