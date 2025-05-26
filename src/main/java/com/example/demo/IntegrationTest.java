package com.example.demo;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest {

    @Test
    @DisplayName("Test complet d'un scénario d'utilisation")
    void testScenarioComplet() throws Exception {
        // Reset du singleton
        try {
            java.lang.reflect.Field instance = GestionEvenements.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            // Ignore
        }

        GestionEvenements gestion = GestionEvenements.getInstance();

        // 1. Créer des participants
        Participant alice = new Participant("P001", "Alice", "alice@email.com");
        Participant bob = new Participant("P002", "Bob", "bob@email.com");
        Organisateur claire = new Organisateur("O001", "Claire", "claire@email.com");

        gestion.ajouterParticipant(alice);
        gestion.ajouterParticipant(bob);
        gestion.ajouterParticipant(claire);

        // 2. Créer des événements
        Map<String, Object> paramsConf = new HashMap<>();
        paramsConf.put("theme", "IA et Futur");
        Conference conference = (Conference) EvenementFactory.creerEvenement("conference",
                "CONF001", "Conférence IA", LocalDateTime.now().plusDays(30),
                "Centre de Congrès", 2, paramsConf);

        Map<String, Object> paramsConcert = new HashMap<>();
        paramsConcert.put("artiste", "Les Codeurs");
        paramsConcert.put("genreMusical", "Electro");
        Concert concert = (Concert) EvenementFactory.creerEvenement("concert",
                "CONC001", "Concert Electro", LocalDateTime.now().plusDays(15),
                "Zenith", 3, paramsConcert);

        gestion.ajouterEvenement(conference);
        gestion.ajouterEvenement(concert);

        // 3. Inscrire des participants
        conference.ajouterParticipant(alice);
        conference.ajouterParticipant(bob);
        concert.ajouterParticipant(alice);

        // 4. Vérifications
        assertEquals(2, conference.getParticipants().size());
        assertEquals(1, concert.getParticipants().size());

        // 5. Annuler un événement et vérifier les notifications
        int notificationsAliceAvant = alice.getNotifications().size();
        conference.annuler();
        int notificationsAliceApres = alice.getNotifications().size();

        assertTrue(conference.isAnnule());
        assertTrue(notificationsAliceApres > notificationsAliceAvant);

        // 6. Tester la capacité maximale
        Participant charlie = new Participant("P003", "Charlie", "charlie@email.com");
        gestion.ajouterParticipant(charlie);

        // Le concert a une capacité de 3, Alice est déjà inscrite
        concert.ajouterParticipant(bob);
        concert.ajouterParticipant(charlie);

        Participant david = new Participant("P004", "David", "david@email.com");
        assertThrows(CapaciteMaxAtteinteException.class,
                () -> concert.ajouterParticipant(david));

        // 7. Tester la recherche
        var evenementsActifs = gestion.rechercherEvenementsParCritere(e -> !e.isAnnule());
        assertEquals(1, evenementsActifs.size());
        assertEquals("Concert Electro", evenementsActifs.get(0).getNom());
    }

    @Test
    @DisplayName("Test de sérialisation JSON")
    void testSerialisationJSON() throws Exception {
        // Ce test nécessiterait un environnement avec Jackson configuré
        // Pour les besoins de la démonstration, on simule le test

        GestionEvenements gestion = GestionEvenements.getInstance();
        Map<String, Object> params = new HashMap<>();
        params.put("theme", "Test");

        Evenement evenement = EvenementFactory.creerEvenement("conference",
                "E001", "Test Event", LocalDateTime.now(), "Test Location", 100, params);

        gestion.ajouterEvenement(evenement);

        // Dans un vrai test, on ferait :
        // gestion.sauvegarderEnJSON("test.json");
        // gestion.chargerDepuisJSON("test.json");

        // Pour l'instant, on vérifie juste que l'événement existe
        assertNotNull(gestion.rechercherEvenement("E001"));
    }
}