package com.example.demo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Conference extends Evenement {
    private String theme;
    private List<Intervenant> intervenants;

    public Conference() {
        super();
        this.intervenants = new ArrayList<>();
    }

    public Conference(String id, String nom, LocalDateTime date, String lieu, int capaciteMax, String theme) {
        super(id, nom, date, lieu, capaciteMax);
        this.theme = theme;
        this.intervenants = new ArrayList<>();
    }

    @Override
    public String afficherDetails() {
        return String.format("Conférence: %s\nThème: %s\nDate: %s\nLieu: %s\nCapacité: %d/%d\nIntervenants: %s",
                nom, theme, date, lieu, participants.size(), capaciteMax,
                intervenants.stream().map(Intervenant::getNom).collect(Collectors.joining(", ")));
    }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    public List<Intervenant> getIntervenants() { return new ArrayList<>(intervenants); }
    public void setIntervenants(List<Intervenant> intervenants) { this.intervenants = new ArrayList<>(intervenants); }
    public void ajouterIntervenant(Intervenant intervenant) { this.intervenants.add(intervenant); }
}
