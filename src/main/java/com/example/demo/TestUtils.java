package com.example.demo;


import java.time.LocalDateTime;

class TestUtils {

    public static Conference creerConferenceTest() {
        return new Conference("TEST001", "Conférence Test",
                LocalDateTime.now().plusDays(1), "Lieu Test", 10, "Thème Test");
    }

    public static Concert creerConcertTest() {
        return new Concert("TEST002", "Concert Test",
                LocalDateTime.now().plusDays(2), "Salle Test", 20, "Artiste Test", "Rock");
    }

    public static Participant creerParticipantTest(String id) {
        return new Participant(id, "Participant " + id, id + "@test.com");
    }

    public static void attendreNotificationAsync(int millisecondes) {
        try {
            Thread.sleep(millisecondes);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
