package com.example.demo;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

class GestionEvenementsTest {

    private GestionEvenements gestionEvenements;
    private Participant participant;
    private Conference conference;

    @BeforeEach
    void setUp() {
        // Reset du singleton pour les tests
        try {
            java.lang.reflect.Field instance = GestionEvenements.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            // Ignore
        }

        gestionEvenements = GestionEvenements.getInstance();
        participant = new Participant("P001", "Test User", "test@email.com");
        conference = new Conference("E001", "Test Conference",
                LocalDateTime.now().plusDays(1), "Test Location", 2, "Test Theme");
    }

    @Test
    @DisplayName("Test d'ajout d'un événement")
    void testAjouterEvenement() throws EvenementDejaExistantException {
        gestionEvenements.ajouterEvenement(conference);

        Evenement evenementRecupere = gestionEvenements.rechercherEvenement("E001");
        assertNotNull(evenementRecupere);
        assertEquals("Test Conference", evenementRecupere.getNom());
    }

    @Test
    @DisplayName("Test d'exception pour événement déjà existant")
    void testEvenementDejaExistant() throws EvenementDejaExistantException {
        gestionEvenements.ajouterEvenement(conference);

        Conference autreConference = new Conference("E001", "Autre Conference",
                LocalDateTime.now().plusDays(2), "Autre Location", 3, "Autre Theme");

        assertThrows(EvenementDejaExistantException.class,
                () -> gestionEvenements.ajouterEvenement(autreConference));
    }

    @Test
    @DisplayName("Test d'inscription d'un participant")
    void testInscriptionParticipant() throws CapaciteMaxAtteinteException {
        conference.ajouterParticipant(participant);

        assertTrue(conference.getParticipants().contains(participant));
        assertEquals(1, conference.getParticipants().size());
    }

    @Test
    @DisplayName("Test d'exception pour capacité maximale atteinte")
    void testCapaciteMaxAtteinte() throws CapaciteMaxAtteinteException {
        Participant p1 = new Participant("P001", "User 1", "user1@email.com");
        Participant p2 = new Participant("P002", "User 2", "user2@email.com");
        Participant p3 = new Participant("P003", "User 3", "user3@email.com");

        conference.ajouterParticipant(p1);
        conference.ajouterParticipant(p2);

        assertThrows(CapaciteMaxAtteinteException.class,
                () -> conference.ajouterParticipant(p3));
    }

    @Test
    @DisplayName("Test de désinscription d'un participant")
    void testDesinscriptionParticipant() throws CapaciteMaxAtteinteException {
        conference.ajouterParticipant(participant);
        conference.retirerParticipant(participant);

        assertFalse(conference.getParticipants().contains(participant));
        assertEquals(0, conference.getParticipants().size());
    }

    @Test
    @DisplayName("Test d'annulation d'événement")
    void testAnnulationEvenement() throws CapaciteMaxAtteinteException {
        conference.ajouterParticipant(participant);
        assertFalse(conference.isAnnule());

        conference.annuler();
        assertTrue(conference.isAnnule());

        // Vérifier qu'on ne peut plus inscrire de participants
        Participant autreParticipant = new Participant("P002", "Autre User", "autre@email.com");
        assertThrows(IllegalStateException.class,
                () -> conference.ajouterParticipant(autreParticipant));
    }

    @Test
    @DisplayName("Test du pattern Observer")
    void testPatternObserver() throws CapaciteMaxAtteinteException {
        conference.ajouterParticipant(participant);

        // Vérifier que le participant est bien observateur
        int notificationsAvant = participant.getNotifications().size();
        conference.annuler();
        int notificationsApres = participant.getNotifications().size();

        assertTrue(notificationsApres > notificationsAvant);
    }

    @Test
    @DisplayName("Test du Factory Pattern")
    void testFactoryPattern() {
        Map<String, Object> params = new HashMap<>();
        params.put("theme", "Test Theme");

        Evenement evenement = EvenementFactory.creerEvenement("conference", "E001",
                "Test Event", LocalDateTime.now(), "Test Location", 100, params);

        assertNotNull(evenement);
        assertTrue(evenement instanceof Conference);
        assertEquals("Test Theme", ((Conference) evenement).getTheme());
    }

    @Test
    @DisplayName("Test du Singleton Pattern")
    void testSingletonPattern() {
        GestionEvenements instance1 = GestionEvenements.getInstance();
        GestionEvenements instance2 = GestionEvenements.getInstance();

        assertSame(instance1, instance2);
    }

    @Test
    @DisplayName("Test de recherche avec critères")
    void testRechercheAvecCriteres() throws EvenementDejaExistantException {
        gestionEvenements.ajouterEvenement(conference);

        // Recherche par nom
        var evenements = gestionEvenements.rechercherEvenementsParCritere(
                e -> e.getNom().contains("Test"));

        assertEquals(1, evenements.size());
        assertEquals("Test Conference", evenements.get(0).getNom());

        // Recherche par type
        var conferences = gestionEvenements.rechercherEvenementsParCritere(
                e -> e instanceof Conference);

        assertEquals(1, conferences.size());
    }

    @Test
    @DisplayName("Test de suppression d'événement")
    void testSuppressionEvenement() throws EvenementDejaExistantException {
        gestionEvenements.ajouterEvenement(conference);
        assertNotNull(gestionEvenements.rechercherEvenement("E001"));

        gestionEvenements.supprimerEvenement("E001");
        assertNull(gestionEvenements.rechercherEvenement("E001"));
    }
}