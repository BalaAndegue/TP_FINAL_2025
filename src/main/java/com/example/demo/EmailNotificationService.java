package com.example.demo;

import java.util.concurrent.CompletableFuture;

class EmailNotificationService implements NotificationService {
    @Override
    public void envoyerNotification(String message) {
        System.out.println("Email envoyé: " + message);
    }

    @Override
    public CompletableFuture<Void> envoyerNotificationAsync(String message) {
        return CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1000); // Simulation d'envoi
                System.out.println("Email envoyé (async): " + message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
