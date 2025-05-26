package com.example.demo;

import java.util.ArrayList;
import java.util.List;

public class Organisateur extends Participant {
    private List<String> evenementsOrganises; // IDs des événements

    public Organisateur() {
        super();
        this.evenementsOrganises = new ArrayList<>();
    }

    public Organisateur(String id, String nom, String email) {
        super(id, nom, email);
        this.evenementsOrganises = new ArrayList<>();
    }

    public void ajouterEvenementOrganise(String evenementId) {
        if (!evenementsOrganises.contains(evenementId)) {
            evenementsOrganises.add(evenementId);
        }
    }

    public List<String> getEvenementsOrganises() { return new ArrayList<>(evenementsOrganises); }
    public void setEvenementsOrganises(List<String> evenementsOrganises) {
        this.evenementsOrganises = new ArrayList<>(evenementsOrganises);
    }
}