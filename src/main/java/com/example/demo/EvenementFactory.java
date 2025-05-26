package com.example.demo;

import java.time.LocalDateTime;
import java.util.Map;

class EvenementFactory {
    public static Evenement creerEvenement(String type, String id, String nom, LocalDateTime date,
                                           String lieu, int capaciteMax, Map<String, Object> parametres) {
        switch (type.toLowerCase()) {
            case "conference":
                String theme = (String) parametres.get("theme");
                return new Conference(id, nom, date, lieu, capaciteMax, theme);
            case "concert":
                String artiste = (String) parametres.get("artiste");
                String genre = (String) parametres.get("genreMusical");
                return new Concert(id, nom, date, lieu, capaciteMax, artiste, genre);
            default:
                throw new IllegalArgumentException("Type d'événement non supporté: " + type);
        }
    }
}
