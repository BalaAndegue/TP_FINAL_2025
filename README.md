# 🎟️ Système de Gestion d'Événements (Java/JavaFX)
## pour cloner:
git clone https://github.com/BalaAndegue/TP_FINAL_2025.git

## naviguer dans le dossier gestion-evenements.git
cd gestion-evenements

## compiler et executer
mvn javafx:run
##
Un système complet de gestion d'événements implémentant des patrons de conception avancés avec une interface JavaFX moderne.

## ✨ Fonctionnalités

- **Gestion multi-types d'événements** (Conférences, Concerts)
- **Inscription des participants** avec contrôle de capacité
- **Système de notification** (Observer Pattern)
- **Persistance JSON** avec Jackson
- **Interface intuitive** avec JavaFX
- **Tests complets** (Unitaires, Intégration)

## 🛠 Architecture Technique

```mermaid
graph TD
    A[Interface JavaFX] --> B[GestionEvenements]
    B --> C[Modèle Evenement]
    B --> D[Modèle Participant]
    C --> E[Conference/Concert]
    D --> F[Organisateur/Participant]
    B --> G[Services JSON]

