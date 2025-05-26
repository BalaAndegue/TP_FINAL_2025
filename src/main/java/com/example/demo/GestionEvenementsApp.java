package com.example.demo;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GestionEvenementsApp extends Application {
    private GestionEvenements gestionEvenements;
    private StackPane contentArea;
    private ObservableList<Evenement> listeEvenements;
    private ObservableList<Participant> listeParticipants;

    // Couleurs modernes
    private final Color PRIMARY_COLOR = Color.web("#4a6fa5");
    private final Color SECONDARY_COLOR = Color.web("#166088");
    private final Color ACCENT_COLOR = Color.web("#4fc3f7");
    private final Color BACKGROUND_COLOR = Color.web("#f8f9fa");
    private final Color TEXT_COLOR = Color.web("#343a40");
    private final Color SIDEBAR_COLOR = Color.web("#2c3e50");

    @Override
    public void start(Stage primaryStage) {
        gestionEvenements = GestionEvenements.getInstance();

        // Initialisation des données observables
        listeEvenements = FXCollections.observableArrayList();
        listeParticipants = FXCollections.observableArrayList();

        // Création de la structure principale
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + toHex(BACKGROUND_COLOR) + ";");

        // Sidebar avec menu latéral
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        // Zone de contenu principale
        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: " + toHex(BACKGROUND_COLOR) + ";");
        root.setCenter(contentArea);

        // Afficher la vue des événements par défaut
        showEvenementsView();

        Scene scene = new Scene(root, 1200, 800);
        scene.setFill(BACKGROUND_COLOR);

        primaryStage.setTitle("Système de Gestion d'Événements");
        primaryStage.setScene(scene);

        // Ajouter une icône à la fenêtre
        try {
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon/app-icon.png")));
        } catch (Exception e) {
            System.out.println("Icône non trouvée, utilisation de l'icône par défaut");
        }

        primaryStage.show();

        // Chargement des données initiales
        chargerDonnees();
    }

    private void chargerDonnees() {
        String fichierEvenements = "evenements.json";
        String fichierParticipants = "participants.json";

        try {
            // Chargement des événements depuis le fichier JSON
            gestionEvenements.chargerDepuisJSON(fichierEvenements);
            listeEvenements.setAll(gestionEvenements.obtenirTousLesEvenements());

            // Chargement des participants depuis un autre fichier (si nécessaire)
            //gestionEvenements.chargerDepuisJSON(fichierParticipants);
            //listeParticipants.setAll(gestionEvenements.obtenirTousLesParticipants());

            afficherAlerte("Chargement", "Données chargées avec succès !", Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            afficherAlerte("Erreur", "Impossible de charger les données !", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }


    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: " + toHex(SIDEBAR_COLOR) + ";");
        sidebar.setPadding(new Insets(20, 10, 20, 10));
        sidebar.setAlignment(Pos.TOP_CENTER);

        // Logo de l'application
        HBox logoBox = new HBox();
        logoBox.setAlignment(Pos.CENTER);
        logoBox.setPadding(new Insets(0, 0, 30, 0));

        try {
            ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/icon/app-icon.png")));
            logo.setFitWidth(40);
            logo.setFitHeight(40);
            logoBox.getChildren().add(logo);
        } catch (Exception e) {
            Label logoText = new Label("EventsApp");
            logoText.setStyle("-fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;");
            logoBox.getChildren().add(logoText);
        }

        // Boutons de navigation
        VBox navButtons = new VBox(5);
        navButtons.setAlignment(Pos.TOP_CENTER);

        Button btnEvenements = createNavButton("Événements", "/icon/event-icon.png");
        Button btnParticipants = createNavButton("Participants", "/icon/user-icon.png");
        Button btnInscriptions = createNavButton("Inscriptions", "/icon/register-icon.png");

        btnEvenements.setOnAction(e -> showEvenementsView());
        btnParticipants.setOnAction(e -> showParticipantsView());
        btnInscriptions.setOnAction(e -> showInscriptionsView());

        navButtons.getChildren().addAll(btnEvenements, btnParticipants, btnInscriptions);

        // Espace flexible pour pousser les éléments vers le haut
        Pane spacer = new Pane();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Section inférieure (paramètres, etc.)
        Button btnParametres = createNavButton("Paramètres", "/icon/settings-icon.png");
        Button btnDeconnexion = createNavButton("Déconnexion", "/icon/logout-icon.png");

        VBox bottomSection = new VBox(5);
        bottomSection.setAlignment(Pos.BOTTOM_CENTER);
        bottomSection.getChildren().addAll(btnParametres, btnDeconnexion);

        sidebar.getChildren().addAll(logoBox, navButtons, spacer, bottomSection);

        return sidebar;
    }

    private Button createNavButton(String text, String iconPath) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 10 15;");

        try {
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
            icon.setFitWidth(20);
            icon.setFitHeight(20);
            button.setGraphic(icon);
            button.setContentDisplay(ContentDisplay.LEFT);
            button.setGraphicTextGap(10);
        } catch (Exception e) {
            System.out.println("Icône " + iconPath + " non trouvée");
        }

        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 10 15;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 10 15;"));

        return button;
    }

    private void showEvenementsView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));
        view.setStyle("-fx-background-color: " + toHex(BACKGROUND_COLOR) + ";");

        // En-tête avec titre et bouton d'action
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(15);

        Label title = new Label("Événements");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: " + toHex(TEXT_COLOR) + ";");

        Button addButton = createActionButton("Nouvel événement", "/icon/add-icon.png");
        addButton.setOnAction(e -> afficherDialogueAjoutEvenement());

        HBox.setHgrow(title, Priority.ALWAYS);
        header.getChildren().addAll(title, addButton);

        // Cartes d'événements
        GridPane cardsContainer = new GridPane();
        cardsContainer.setHgap(20);
        cardsContainer.setVgap(20);
        cardsContainer.setPadding(new Insets(10));

        // Pour chaque événement, créer une carte
        int row = 0, col = 0;
        for (Evenement event : listeEvenements) {
            VBox card = createEventCard(event);
            cardsContainer.add(card, col, row);

            col++;
            if (col > 2) { // 3 cartes par ligne
                col = 0;
                row++;
            }
        }

        ScrollPane scrollPane = new ScrollPane(cardsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-padding: 10;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        view.getChildren().addAll(header, scrollPane);
        contentArea.getChildren().setAll(view);
    }

    private void afficherDialogueAjoutEvenement() {
        Dialog<Evenement> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un nouvel événement");
        dialog.setHeaderText("Choisissez le type d'événement");

        // Boutons de confirmation et annulation
        ButtonType ajouterButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ajouterButtonType, ButtonType.CANCEL);

        // Formulaire
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Conférence", "Concert");
        typeComboBox.setValue("Conférence"); // Valeur par défaut

        TextField idField = new TextField();
        idField.setPromptText("ID de l'événement");

        TextField nomField = new TextField();
        nomField.setPromptText("Nom de l'événement");

        DatePicker datePicker = new DatePicker();
        TextField heureField = new TextField();
        heureField.setPromptText("HH:mm");

        TextField lieuField = new TextField();
        lieuField.setPromptText("Lieu");

        TextField capaciteField = new TextField();
        capaciteField.setPromptText("Capacité maximale");

        TextField themeField = new TextField(); // Spécifique à `Conference`
        themeField.setPromptText("Thème de la conférence");

        TextField artisteField = new TextField(); // Spécifique à `Concert`
        artisteField.setPromptText("Nom de l'artiste");

        TextField genreField = new TextField(); // Spécifique à `Concert`
        genreField.setPromptText("Genre musical");

        CheckBox annuleCheckBox = new CheckBox("Annulé");

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeComboBox, 1, 0);
        grid.add(new Label("ID:"), 0, 1);
        grid.add(idField, 1, 1);
        grid.add(new Label("Nom:"), 0, 2);
        grid.add(nomField, 1, 2);
        grid.add(new Label("Date:"), 0, 3);
        grid.add(datePicker, 1, 3);
        grid.add(new Label("Heure:"), 0, 4);
        grid.add(heureField, 1, 4);
        grid.add(new Label("Lieu:"), 0, 5);
        grid.add(lieuField, 1, 5);
        grid.add(new Label("Capacité:"), 0, 6);
        grid.add(capaciteField, 1, 6);
        grid.add(new Label("Thème Conférence:"), 0, 7);
        grid.add(themeField, 1, 7);
        grid.add(new Label("Artiste:"), 0, 8);
        grid.add(artisteField, 1, 8);
        grid.add(new Label("Genre musical:"), 0, 9);
        grid.add(genreField, 1, 9);
        grid.add(new Label("Annulé:"), 0, 10);
        grid.add(annuleCheckBox, 1, 10);

        dialog.getDialogPane().setContent(grid);

        // Vérification et création de l'événement
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ajouterButtonType) {
                String id = idField.getText();
                String type = typeComboBox.getValue();

                if ("Conférence".equals(type)) {
                    String nom = nomField.getText();
                    LocalDate date = datePicker.getValue();
                    String heureStr = heureField.getText();
                    String lieu = lieuField.getText();
                    int capacite = Integer.parseInt(capaciteField.getText());
                    String theme = themeField.getText();
                    boolean annule = annuleCheckBox.isSelected();

                    if (nom.isEmpty() || date == null || heureStr.isEmpty() || lieu.isEmpty()) {
                        return null;
                    }

                    LocalTime heure = LocalTime.parse(heureStr);
                    LocalDateTime dateTime = date.atTime(heure);
                    Evenement conference = new Conference(id, nom, dateTime, lieu, capacite, theme);
                    try{
                        gestionEvenements.ajouterEvenement(conference);
                        gestionEvenements.sauvegarderEnJSON("evenements.json");
                        System.out.println("conference enregistre en json");
                    }catch (EvenementDejaExistantException e){
                        e.printStackTrace();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return conference;
                } else {
                    String nom = nomField.getText();
                    LocalDate date = datePicker.getValue();
                    String heureStr = heureField.getText();
                    String lieu = lieuField.getText();
                    int capacite = Integer.parseInt(capaciteField.getText());
                    String artiste = artisteField.getText();
                    String genre = genreField.getText();

                    if (nom.isEmpty() || date == null || heureStr.isEmpty() || lieu.isEmpty() || artiste.isEmpty() || genre.isEmpty()) {
                        return null;
                    }

                    LocalTime heure = LocalTime.parse(heureStr);
                    LocalDateTime dateTime = date.atTime(heure);

                    Evenement concert = new Concert(id, nom, dateTime, lieu, capacite, artiste, genre);

                    try{
                        gestionEvenements.ajouterEvenement(concert);
                        gestionEvenements.sauvegarderEnJSON("evenements.json");
                        System.out.println("concert enregistre en json");
                    }catch (EvenementDejaExistantException e){
                        e.printStackTrace();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    return concert;

                }
            }
            return null;
        });

        Optional<Evenement> result = dialog.showAndWait();
        result.ifPresent(evenement -> {
            listeEvenements.add(evenement);
            afficherAlerte("Succès", "Événement ajouté avec succès!", Alert.AlertType.INFORMATION);
        });
    }





    public String toHex(Color color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);

        return String.format("#%02X%02X%02X", r, g, b);
    }

    private VBox createEventCard(Evenement event) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        card.setPrefWidth(350);

        // En-tête de la carte
        HBox cardHeader = new HBox();
        cardHeader.setAlignment(Pos.CENTER_LEFT);
        cardHeader.setSpacing(10);

        // Icône selon le type d'événement
        String iconPath = event instanceof Conference ? "/icon/conference-icon.png" : "/icon/concert-icon.png";
        try {
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
            icon.setFitWidth(30);
            icon.setFitHeight(30);
            cardHeader.getChildren().add(icon);
        } catch (Exception e) {
            System.out.println("Icône non trouvée: " + iconPath);
        }

        Label title = new Label(event.getNom());
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: " + toHex(TEXT_COLOR) + ";");
        cardHeader.getChildren().add(title);

        // Détails de l'événement
        VBox details = new VBox(5);

        HBox dateBox = new HBox(5);
        dateBox.setAlignment(Pos.CENTER_LEFT);
        dateBox.getChildren().addAll(
                createDetailIcon("/icon/calendar-icon.png"),
                createDetailLabel(event.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
        );

        HBox locationBox = new HBox(5);
        locationBox.setAlignment(Pos.CENTER_LEFT);
        locationBox.getChildren().addAll(
                createDetailIcon("/icon/location-icon.png"),
                createDetailLabel(event.getLieu())
        );

        HBox capacityBox = new HBox(5);
        capacityBox.setAlignment(Pos.CENTER_LEFT);
        capacityBox.getChildren().addAll(
                createDetailIcon("/icon/users-icon.png"),
                createDetailLabel(event.getParticipants().size() + "/" + event.getCapaciteMax() + " participants")
        );

        details.getChildren().addAll(dateBox, locationBox, capacityBox);

        // Boutons d'action
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button detailsBtn = createSmallButton("Détails", "/icon/info-icon.png");
        detailsBtn.setOnAction(e -> afficherDetailsEvenement(event));

        Button deleteBtn = createSmallButton("Supprimer", "/icon/delete-icon.png");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> supprimerEvenementSelectionne(event));

        buttons.getChildren().addAll(detailsBtn, deleteBtn);

        card.getChildren().addAll(cardHeader, details, buttons);
        return card;
    }

    private void supprimerEvenementSelectionne(Evenement event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Suppression d'événement");
        alert.setHeaderText("Voulez-vous vraiment supprimer cet événement ?");
        alert.setContentText(event.getNom());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            listeEvenements.remove(event);
            afficherAlerte("Succès", "Événement supprimé avec succès!", Alert.AlertType.INFORMATION);
            rafraichirTables();
        }
    }

    private void afficherDetailsEvenement(Evenement event) {
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Détails de l'événement");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");

        Label titleLabel = new Label(event.getNom());
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        String iconPath = event instanceof Conference ? "/icon/conference-icon.png" : "/icon/concert-icon.png";
        try {
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
            icon.setFitWidth(40);
            icon.setFitHeight(40);
            root.getChildren().add(icon);
        } catch (Exception e) {
            System.out.println("Icône non trouvée: " + iconPath);
        }

        VBox details = new VBox(10);
        details.getChildren().addAll(
                new Label("Date : " + event.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))),
                new Label("Lieu : " + event.getLieu()),
                new Label("Capacité : " + event.getParticipants().size() + "/" + event.getCapaciteMax())
        );

        if (event instanceof Conference) {
            details.getChildren().add(new Label("Thème : " + ((Conference) event).getTheme()));
        } else if (event instanceof Concert) {
            details.getChildren().addAll(
                    new Label("Artiste : " + ((Concert) event).getArtiste()),
                    new Label("Genre musical : " + ((Concert) event).getGenreMusical())
            );
        }

        Button closeButton = new Button("Fermer");
        closeButton.setOnAction(e -> detailsStage.close());

        root.getChildren().addAll(titleLabel, details, closeButton);

        Scene scene = new Scene(root, 400, 450);
        detailsStage.setScene(scene);
        detailsStage.show();
    }


    private void showParticipantsView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));
        view.setStyle("-fx-background-color: " + toHex(BACKGROUND_COLOR) + ";");

        // En-tête avec titre et bouton d'action
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(15);

        Label title = new Label("Participants");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: " + toHex(TEXT_COLOR) + ";");

        Button addButton = createActionButton("Nouveau participant", "/icon/add-user-icon.png");
        addButton.setOnAction(e -> afficherDialogueAjoutParticipant());

        HBox.setHgrow(title, Priority.ALWAYS);
        header.getChildren().addAll(title, addButton);

        // Liste des participants avec des cartes
        FlowPane participantsContainer = new FlowPane();
        participantsContainer.setHgap(20);
        participantsContainer.setVgap(20);
        participantsContainer.setPadding(new Insets(10));

        for (Participant participant : listeParticipants) {
            participantsContainer.getChildren().add(createParticipantCard(participant));
        }

        ScrollPane scrollPane = new ScrollPane(participantsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-padding: 10;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        view.getChildren().addAll(header, scrollPane);
        contentArea.getChildren().setAll(view);
    }

    private void afficherDialogueAjoutParticipant() {
        Dialog<Participant> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un participant");
        dialog.setHeaderText("Remplissez les informations du participant");

        // Boutons de confirmation et annulation
        ButtonType ajouterButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ajouterButtonType, ButtonType.CANCEL);

        // Formulaire
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField idField = new TextField();
        idField.setPromptText("ID du participant");

        TextField nomField = new TextField();
        nomField.setPromptText("Nom");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        grid.add(new Label("ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Nom:"), 0, 1);
        grid.add(nomField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Création du participant
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ajouterButtonType) {
                String id = idField.getText();
                String nom = nomField.getText();
                String email = emailField.getText();

                if (id.isEmpty() || nom.isEmpty() || email.isEmpty()) {
                    afficherAlerte("Erreur", "Tous les champs doivent être remplis!", Alert.AlertType.ERROR);
                    return null;
                }
                ParticipantObserver participant = new Participant(id, nom, email);

                try{
                    gestionEvenements.ajouterParticipant((Participant) participant);
                    gestionEvenements.sauvegarderEnJSON("participants.json");
                    System.out.println("participant enregistre en json");
                }catch (ParticipantInexistantException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                return new Participant(id, nom, email);
            }
            return null;
        });

        Optional<Participant> result = dialog.showAndWait();
        result.ifPresent(participant -> {
            listeParticipants.add(participant);
            afficherAlerte("Succès", "Participant ajouté avec succès!", Alert.AlertType.INFORMATION);
            rafraichirTables();
        });
    }

    private void rafraichirTables() {
        showParticipantsView();
        showInscriptionsView();
    }



    private VBox createParticipantCard(Participant participant) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        card.setPrefWidth(300);

        // En-tête avec avatar et nom
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        // Avatar (simplifié)
        Circle avatar = new Circle(25);
        avatar.setFill(participant instanceof Organisateur ? SECONDARY_COLOR : PRIMARY_COLOR);

        Label name = new Label(participant.getNom());
        name.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        header.getChildren().addAll(avatar, name);

        // Détails
        VBox details = new VBox(5);

        HBox emailBox = new HBox(5);
        emailBox.setAlignment(Pos.CENTER_LEFT);
        emailBox.getChildren().addAll(
                createDetailIcon("/icon/email-icon.png"),
                createDetailLabel(participant.getEmail())
        );

        HBox typeBox = new HBox(5);
        typeBox.setAlignment(Pos.CENTER_LEFT);
        typeBox.getChildren().addAll(
                createDetailIcon("/icon/type-icon.png"),
                createDetailLabel(participant instanceof Organisateur ? "Organisateur" : "Participant")
        );

        details.getChildren().addAll(emailBox, typeBox);

        card.getChildren().addAll(header, details);
        return card;
    }

    private void showInscriptionsView() {
        VBox view = new VBox(20);
        view.setPadding(new Insets(20));
        view.setStyle("-fx-background-color: " + toHex(BACKGROUND_COLOR) + ";");

        Label title = new Label("Gestion des Inscriptions");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: " + toHex(TEXT_COLOR) + ";");

        // Carte avec fond blanc et coins arrondis
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        card.setMaxWidth(800);

        // ComboBox pour sélectionner un événement avec style
        ComboBox<Evenement> comboEvenements = new ComboBox<>();
        comboEvenements.setItems(listeEvenements);
        comboEvenements.setConverter(new StringConverter<Evenement>() {
            @Override
            public String toString(Evenement evenement) {
                return evenement != null ? evenement.getNom() : "";
            }

            @Override
            public Evenement fromString(String string) {
                return null;
            }
        });
        comboEvenements.setPromptText("Sélectionnez un événement");
        comboEvenements.setStyle("-fx-background-color: #f1f3f4; -fx-background-radius: 5;");

        // ComboBox pour sélectionner un participant avec style
        ComboBox<Participant> comboParticipants = new ComboBox<>();
        comboParticipants.setItems(listeParticipants);
        comboParticipants.setConverter(new StringConverter<Participant>() {
            @Override
            public String toString(Participant participant) {
                return participant != null ? participant.getNom() : "";
            }

            @Override
            public Participant fromString(String string) {
                return null;
            }
        });
        comboParticipants.setPromptText("Sélectionnez un participant");
        comboParticipants.setStyle("-fx-background-color: #f1f3f4; -fx-background-radius: 5;");

        Button btnInscrire = createActionButton("Inscrire", "/icon/register-icon.png");
        Button btnDesinscrire = createActionButton("Désinscrire", "/icon/unregister-icon.png");

        btnInscrire.setOnAction(e -> {
            Evenement evenement = comboEvenements.getValue();
            Participant participant = comboParticipants.getValue();
            if (evenement != null && participant != null) {
                try {
                    evenement.ajouterParticipant(participant);
                    afficherAlerte("Succès", "Participant inscrit avec succès!", Alert.AlertType.INFORMATION);
                    rafraichirTables();
                } catch (CapaciteMaxAtteinteException ex) {
                    afficherAlerte("Erreur", ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });

        btnDesinscrire.setOnAction(e -> {
            Evenement evenement = comboEvenements.getValue();
            Participant participant = comboParticipants.getValue();
            if (evenement != null && participant != null) {
                evenement.retirerParticipant(participant);
                afficherAlerte("Succès", "Participant désinscrit avec succès!", Alert.AlertType.INFORMATION);
                rafraichirTables();
            }
        });

        // GridPane pour un alignement propre
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        grid.add(new Label("Événement:"), 0, 0);
        grid.add(comboEvenements, 1, 0);
        grid.add(new Label("Participant:"), 0, 1);
        grid.add(comboParticipants, 1, 1);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(btnInscrire, btnDesinscrire);

        grid.add(buttonBox, 0, 2, 2, 1);

        card.getChildren().addAll(grid);
        view.getChildren().addAll(title, card);
        view.setAlignment(Pos.TOP_CENTER);

        contentArea.getChildren().setAll(view);
    }

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private Button createActionButton(String text, String iconPath) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + toHex(PRIMARY_COLOR) + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 10 20 10 20;");

        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + toHex(SECONDARY_COLOR) + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 10 20 10 20;"));

        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + toHex(PRIMARY_COLOR) + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 10 20 10 20;"));

        try {
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
            icon.setFitWidth(16);
            icon.setFitHeight(16);
            button.setGraphic(icon);
            button.setContentDisplay(ContentDisplay.LEFT);
            button.setGraphicTextGap(10);
        } catch (Exception e) {
            System.out.println("Icône " + iconPath + " non trouvée");
        }

        return button;
    }

    private Button createSmallButton(String text, String iconPath) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + toHex(PRIMARY_COLOR) + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 12; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 5 10 5 10;");

        try {
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
            icon.setFitWidth(12);
            icon.setFitHeight(12);
            button.setGraphic(icon);
            button.setContentDisplay(ContentDisplay.LEFT);
            button.setGraphicTextGap(5);
        } catch (Exception e) {
            System.out.println("Icône " + iconPath + " non trouvée");
        }

        return button;
    }

    private ImageView createDetailIcon(String iconPath) {
        try {
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
            icon.setFitWidth(16);
            icon.setFitHeight(16);
            return icon;
        } catch (Exception e) {
            System.out.println("Icône " + iconPath + " non trouvée");
            return new ImageView();
        }
    }

    private Label createDetailLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + toHex(TEXT_COLOR) + "; -fx-font-size: 14;");
        return label;
    }

    // Les méthodes suivantes restent inchangées (afficherDialogueAjoutEvenement, afficherDialogueAjoutParticipant, etc.)
    // ... (copier les méthodes existantes sans modification)

    public static void main(String[] args) {
        launch(args);
    }
}
