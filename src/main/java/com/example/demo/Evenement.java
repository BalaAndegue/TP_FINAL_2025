package com.example.demo;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Conference.class, name = "conference"),
        @JsonSubTypes.Type(value = Concert.class, name = "concert")
})
public abstract class Evenement implements EvenementObservable {
    protected String id;
    protected String nom;
    protected LocalDateTime date;
    protected String lieu;
    protected int capaciteMax;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected List<Participant> participants;
    protected List<ParticipantObserver> observers;
    protected boolean annule;

    public Evenement() {
        this.participants = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.annule = false;
    }

    public Evenement(String id, String nom, LocalDateTime date, String lieu, int capaciteMax) {
        this();
        this.id = id;
        this.nom = nom;
        this.date = date;
        this.lieu = lieu;
        this.capaciteMax = capaciteMax;
    }

    public void ajouterParticipant(Participant participant) throws CapaciteMaxAtteinteException {
        if (annule) {
            throw new IllegalStateException("Impossible d'ajouter un participant à un événement annulé");
        }
        if (participants.size() >= capaciteMax) {
            throw new CapaciteMaxAtteinteException("Capacité maximale atteinte pour l'événement: " + nom);
        }
        if (!participants.contains(participant)) {
            participants.add(participant);
            ajouterObserver(participant);
        }
    }

    public void retirerParticipant(Participant participant) {
        participants.remove(participant);
        retirerObserver(participant);
    }

    public void annuler() {
        this.annule = true;
        notifierObservers("L'événement '" + nom + "' a été annulé.");
    }

    public abstract String afficherDetails();

    // Pattern Observer
    @Override
    public void ajouterObserver(ParticipantObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void retirerObserver(ParticipantObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifierObservers(String message) {
        observers.forEach(observer -> observer.update(message, this));
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }
    public int getCapaciteMax() { return capaciteMax; }
    public void setCapaciteMax(int capaciteMax) { this.capaciteMax = capaciteMax; }
    public List<Participant> getParticipants() { return new ArrayList<>(participants); }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }
    public boolean isAnnule() { return annule; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Evenement evenement = (Evenement) obj;
        return Objects.equals(id, evenement.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
