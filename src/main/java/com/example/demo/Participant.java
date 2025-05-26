package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Participant implements ParticipantObserver {
    protected String id;
    protected String nom;
    protected String email;
    protected List<String> notifications;

    public Participant() {
        this.notifications = new ArrayList<>();
    }

    public Participant(String id, String nom, String email) {
        this();
        this.id = id;
        this.nom = nom;
        this.email = email;
    }

    @Override
    public void update(String message, Evenement evenement) {
        String notification = String.format("[%s] %s",
                java.time.LocalDateTime.now().toString(), message);
        notifications.add(notification);
        System.out.println("Notification pour " + nom + ": " + message);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<String> getNotifications() { return new ArrayList<>(notifications); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Participant that = (Participant) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}