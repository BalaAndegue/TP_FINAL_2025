package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GestionEvenements {
    private static GestionEvenements instance;
    private Map<String, Evenement> evenements;
    private Map<String, Participant> participants;
    private NotificationService notificationService;
    private ObjectMapper objectMapper;

    private GestionEvenements() {
        this.evenements = new HashMap<>();
        this.participants = new HashMap<>();
        this.notificationService = new EmailNotificationService();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static synchronized GestionEvenements getInstance() {
        if (instance == null) {
            instance = new GestionEvenements();
        }
        return instance;
    }

    public void ajouterEvenement(Evenement evenement) throws EvenementDejaExistantException {
        if (evenements.containsKey(evenement.getId())) {
            throw new EvenementDejaExistantException("Un événement avec l'ID " + evenement.getId() + " existe déjà");
        }
        evenements.put(evenement.getId(), evenement);
    }

    public void supprimerEvenement(String id) {
        Evenement evenement = evenements.get(id);
        if (evenement != null) {
            evenement.annuler();
            evenements.remove(id);
        }
    }

    public Evenement rechercherEvenement(String id) {
        return evenements.get(id);
    }

    public List<Evenement> obtenirTousLesEvenements() {
        return new ArrayList<>(evenements.values());
    }

    public List<Evenement> rechercherEvenementsParCritere(java.util.function.Predicate<Evenement> critere) {
        return evenements.values().stream()
                .filter(critere)
                .collect(Collectors.toList());
    }

    public void ajouterParticipant(Participant participant) throws ParticipantInexistantException{
        participants.put(participant.getId(), participant);
    }

    public Participant obtenirParticipant(String id) {
        return participants.get(id);
    }

    public List<Participant> obtenirTousLesParticipants() {
        return new ArrayList<>(participants.values());
    }

    // Sérialisation JSON
    public void sauvegarderEnJSON(String fichier) throws IOException {
        objectMapper.writeValue(new File(fichier), evenements);
    }

    public void chargerDepuisJSON(String fichier) throws IOException {
        java.lang.reflect.Type type = new com.fasterxml.jackson.core.type.TypeReference<Map<String, Evenement>>(){}.getType();
        Map<String, Evenement> evenementsCharges = objectMapper.readValue(new File(fichier),
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, Evenement>>(){});
        this.evenements.putAll(evenementsCharges);
    }
}