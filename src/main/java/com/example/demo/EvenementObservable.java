package com.example.demo;

interface EvenementObservable {
    void ajouterObserver(ParticipantObserver observer);
    void retirerObserver(ParticipantObserver observer);
    void notifierObservers(String message);
}