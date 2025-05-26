package com.example.demo;

import java.util.concurrent.CompletableFuture;

interface NotificationService {
    void envoyerNotification(String message);
    CompletableFuture<Void> envoyerNotificationAsync(String message);
}