package app;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import service.BibliothequeManager;



import java.time.temporal.ChronoUnit;

import model.Adherent;
import model.Livre;
import model.Magazine;
import model.Document;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import model.Emprunt;
import java.util.List;
import service.DatabaseManager;
import service.BibliothequeException;


public class MainFX extends Application {
	private DatabaseManager db = new DatabaseManager();
	private BibliothequeManager manager;
    
	@Override
    public void start(Stage stage) {
        // 1. Initialisation
        db.connect();
        db.createTables();
        manager = new BibliothequeManager(db);
        
        /*
        // Une simulation pour le retard
        Document doc = manager.chercherDocumentParTitreUnique("T1");
        Adherent jean = manager.chercherAdherentParEmail("e1");
        
        if (doc != null && jean != null) {
             boolean existeDeja = manager.getEmprunts().stream()
                 .anyMatch(e -> e.getDocument().getId().equals(doc.getId()) && e.getDateRetourReelle() == null);
             if (!existeDeja) {
                 LocalDate dateEmpruntPassée = LocalDate.now().minusWeeks(4);
                 LocalDate dateRetourPrevuePassée = dateEmpruntPassée.plusWeeks(3);
                 db.insertEmprunt(doc.getId(), jean.getId(), dateEmpruntPassée, dateRetourPrevuePassée);
                 db.updateDocumentDisponibilite(doc.getId(), false);
                 Emprunt empruntRetard = new Emprunt(doc, jean, dateEmpruntPassée);
                 empruntRetard.setDateRetourPrevue(dateRetourPrevuePassée);
                 manager.getEmprunts().add(empruntRetard);
                 doc.setStatutDocument(false);
                 System.out.println("Simulation retard activée.");
             }
        }
        */

        // 2. Création des Boutons 
        
        // Colonne Documents 
        Button btnAfficherDocs = new Button("📚 Voir les documents");
        Button btnAjouterDoc = new Button("➕ Ajouter un document");
        Button btnModifierDoc = new Button("🖋️ Modifier un document");
        Button btnSupprimerDoc = new Button("🗑️ Supprimer un document");
        
        Button btnRechercheTitre = new Button("🔎 Chercher un doc. par titre");
        Button btnRechercheAuteur = new Button("🔎 Chercher un doc. par auteur");
        Button btnRechercheGenre = new Button("🔎 Chercher un doc. par genre");

        // Colonne Adhérents
        Button btnAfficherAdh = new Button("👥 Voir les adhérents");
        Button btnAjouterAdh = new Button("➕ Ajouter un adhérent");
        Button btnModifierAdh = new Button("🖋️ Modifier un adhérent");
        Button btnSupprimerAdh = new Button("🗑️ Supprimer un adhérent");
        
        Button btnHistorique = new Button("📜 Historique d’un adhérent");
        Button btnVoirStatutAdh = new Button("🔍 Voir le statut d’un adhérent");

        Button btnVoirRetards = new Button("⏰ Voir les retards"); 

        // Colonne Circulation
        Button btnEmprunter = new Button("📖 Emprunter un document");
        Button btnRetourner = new Button("🔁 Retourner un document");
        Button btnVoirEmprunts = new Button("⏳ Voir les emprunts en cours");
        
        Button btnQuitter = new Button("❌ Quitter");

        // 3. Actions 
        btnAfficherDocs.setOnAction(e -> afficherFenetreDocuments());
        btnAfficherAdh.setOnAction(e -> afficherFenetreAdherents());
        btnAjouterDoc.setOnAction(e -> afficherFenetreAjoutDocument());     
        btnAjouterAdh.setOnAction(e -> afficherFenetreAjoutAdherent());
        btnEmprunter.setOnAction(e -> afficherFenetreEmprunterDocument());
        btnRetourner.setOnAction(e -> afficherFenetreRetourDocument());
        btnVoirEmprunts.setOnAction(e -> afficherFenetreEmpruntsEnCours());
        btnVoirRetards.setOnAction(e -> afficherFenetreRetards());
        btnHistorique.setOnAction(e -> afficherFenetreHistoriqueAdherent());
        btnSupprimerAdh.setOnAction(e -> afficherFenetreSupprimerAdherent());
        btnSupprimerDoc.setOnAction(e -> afficherFenetreSupprimerDocument());
        btnModifierAdh.setOnAction(e -> afficherFenetreModifierAdherent());
        btnModifierDoc.setOnAction(e -> afficherFenetreModifierDocument());
        btnRechercheGenre.setOnAction(e -> afficherFenetreRechercheGenre());
        btnRechercheAuteur.setOnAction(e -> afficherFenetreRechercheAuteur());
        btnVoirStatutAdh.setOnAction(e -> afficherFenetreStatutAdherent());
        btnRechercheTitre.setOnAction(e -> afficherFenetreRechercheTitre());
        btnQuitter.setOnAction(e -> stage.close());
        
        // 4. Styles Couleurs
        btnAjouterDoc.getStyleClass().add("button-green");
        btnAjouterAdh.getStyleClass().add("button-green");
        btnModifierDoc.getStyleClass().add("button-yellow");
        btnModifierAdh.getStyleClass().add("button-yellow");
        btnSupprimerDoc.getStyleClass().add("button-red");
        btnSupprimerAdh.getStyleClass().add("button-red");
        btnQuitter.getStyleClass().add("button-red");

        
        // 5. mise en page
        //Carte 1 : Documents
        Label lblDocs = new Label("DOCUMENTS");
        lblDocs.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        
        VBox cardDocs = new VBox(10, 
            lblDocs, 
            new Separator(), 
            btnAfficherDocs, btnAjouterDoc, btnModifierDoc, btnSupprimerDoc,
            new Label(" "), // Espace
            btnRechercheTitre, btnRechercheAuteur, btnRechercheGenre
        );
        cardDocs.getStyleClass().add("glass-pane");
        
        //Carte 2 : Adherents 
        Label lblAdh = new Label("ADHÉRENTS");
        lblAdh.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        
        VBox cardAdh = new VBox(10, 
            lblAdh, 
            new Separator(),
            btnAfficherAdh, btnAjouterAdh, btnModifierAdh, btnSupprimerAdh,
            new Label(" "),
            btnHistorique, btnVoirStatutAdh,
            btnVoirRetards // 
        );
        cardAdh.getStyleClass().add("glass-pane");

        // Carte 3
        Label lblCirc = new Label("CIRCULATION");
        lblCirc.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        
        VBox cardCirc = new VBox(10, 
            lblCirc, 
            new Separator(),
            btnEmprunter, btnRetourner, btnVoirEmprunts,
            
            btnQuitter // 
        );
        cardCirc.getStyleClass().add("glass-pane");

        // Alignement des boutons
        // La boucle force tous les boutons à prendre la largeur max de la carte
        for (javafx.scene.Node node : cardDocs.getChildren()) { if (node instanceof Button) ((Button)node).setMaxWidth(Double.MAX_VALUE); }
        for (javafx.scene.Node node : cardAdh.getChildren()) { if (node instanceof Button) ((Button)node).setMaxWidth(Double.MAX_VALUE); }
        for (javafx.scene.Node node : cardCirc.getChildren()) { if (node instanceof Button) ((Button)node).setMaxWidth(Double.MAX_VALUE); }

        // Assemblage des 3 colonnes
        HBox dashboard = new HBox(20, cardDocs, cardAdh, cardCirc);
        dashboard.setAlignment(Pos.CENTER);
        
        // Titre Principal 
        Label titreApp = new Label("📖  Système de Gestion de Bibliothèque");
        titreApp.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #3a3a3a;");
        
        //racine globale
   
        VBox root = new VBox(30, titreApp, dashboard); 
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        // Scène : STYLE
        Scene scene = new Scene(root, 1100, 600);
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS non trouvé sur le classpath !");
        }

        
        stage.setTitle("Gestion de Bibliothèque - Accueil");
        stage.setScene(scene);
        stage.show();
    }

    
    
    
    private void afficherFenetreAjoutDocument() {
        Stage fenetre = new Stage();
        fenetre.setTitle("Ajouter un document");

        // ComboBox pour choisir le type
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Livre", "Magazine");
        typeCombo.setValue("Livre");// Par défaut

        TextField titreField = new TextField();
        titreField.setPromptText("Titre");

        TextField auteurField = new TextField();
        auteurField.setPromptText("Auteur");

        TextField genreField = new TextField();
        genreField.setPromptText("Genre");

        //Champs pour les Livres (par defaut)
        TextField isbnField = new TextField();
        isbnField.setPromptText("ISBN (optionnel)");
        
        TextField nbPagesField = new TextField();
        nbPagesField.setPromptText("Nombre de pages (optionnel)");

        // Champs pour les Magazines 
        TextField numeroField = new TextField();
        numeroField.setPromptText("Numéro");
        
        TextField periodiciteField = new TextField();
        periodiciteField.setPromptText("Périodicité (en jours)");

        // Conteneur pour les champs specifiques
        VBox champsSpecifiques = new VBox(10);
        champsSpecifiques.getChildren().addAll(
            new Label("ISBN :"), isbnField,
            new Label("Nombre de pages :"), nbPagesField
        );

        // Gerer le changement de type
        typeCombo.setOnAction(e -> {
            String typeSelectionne = typeCombo.getValue();
            champsSpecifiques.getChildren().clear();
            
            if ("Livre".equals(typeSelectionne)) {
                champsSpecifiques.getChildren().addAll(
                    new Label("ISBN :"), isbnField,
                    new Label("Nombre de pages :"), nbPagesField
                );
            } else if ("Magazine".equals(typeSelectionne)) {
                champsSpecifiques.getChildren().addAll(
                    new Label("Numéro :"), numeroField,
                    new Label("Périodicité (jours) :"), periodiciteField
                );
            }
        });

        Button enregistrerBtn = new Button("Enregistrer");
        enregistrerBtn.setDefaultButton(true);

        enregistrerBtn.setOnAction(ev -> {
            String type = typeCombo.getValue();
            String titre = titreField.getText().trim();
            String auteur = auteurField.getText().trim();
            String genre = genreField.getText().trim();

            if (titre.isEmpty() || auteur.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Titre et Auteur sont obligatoires.").showAndWait();
                return;
            }

            try {
                Document nouveauDoc;

                if ("Livre".equals(type)) {                    
                    // 1. On crée d'abord le livre de base
                    Livre l = new Livre(titre, auteur);

                    // 2. On récupère les textes
                    String isbn = isbnField.getText().trim();
                    String nbPagesStr = nbPagesField.getText().trim();

                    // 3. On remplit l'ISBN seulement s'il est saisi
                    if (!isbn.isEmpty()) {
                        l.setIsbn(isbn);
                    }

                    // 4. On remplit les pages seulement si saisies
                    if (!nbPagesStr.isEmpty()) {
                        try {
                        	int pages = Integer.parseInt(nbPagesStr);            
                            if (pages <= 0) {
                                new Alert(Alert.AlertType.ERROR, "Le nombre de pages doit être positif.").showAndWait();
                                return; 
                            }
                            l.setNbPages(pages);
                        } catch (NumberFormatException ex) {
                            new Alert(Alert.AlertType.ERROR, "Le nombre de pages doit être un entier.").showAndWait();
                            return; // Stop si erreur
                        }
                    }
                    nouveauDoc = l;

                } else { 
                    // LOGIQUE magazine 
                    // 1. On crée le magazine de base
                    Magazine m = new Magazine(titre, auteur);

                    String numeroStr = numeroField.getText().trim();
                    String periodiciteStr = periodiciteField.getText().trim();
                    
                    // 2. On remplit le numéro si saisi
                    if (!numeroStr.isEmpty()) {
                        try {
                        	int num = Integer.parseInt(numeroStr);                          
                            if (num <= 0) {
                                new Alert(Alert.AlertType.ERROR, "Le numéro doit être positif.").showAndWait();
                                return;
                            }
                            m.setNumero(num);
                        } catch (NumberFormatException ex) {
                            new Alert(Alert.AlertType.ERROR, "Le numéro doit être un entier.").showAndWait();
                            return;
                        }
                    }

                    // 3. On remplit la périodicité si saisie
                    if (!periodiciteStr.isEmpty()) {
                        try {
                        	int per = Integer.parseInt(periodiciteStr);
                            
                            if (per <= 0) {
                                new Alert(Alert.AlertType.ERROR, "La périodicité doit être positive.").showAndWait();
                                return;
                            }
                            m.setPeriodicite(per);
                        } catch (NumberFormatException ex) {
                            new Alert(Alert.AlertType.ERROR, "La périodicité doit être un entier.").showAndWait();
                            return;
                        }
                    }
                    nouveauDoc = m;
                }

                // Ajout du genre 
                nouveauDoc.setGenre(genre);

                // Enregistrement via le Manager
                String resultat = manager.ajouterDocument(nouveauDoc);

                Alert.AlertType typeAlert = resultat.startsWith("✅")
                    ? Alert.AlertType.INFORMATION
                    : Alert.AlertType.WARNING;

                new Alert(typeAlert, resultat).showAndWait();

                // Fermer la fenêtre si succès
                if (resultat.startsWith("✅")) {
                    fenetre.close();
                }

            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Erreur inattendue : " + ex.getMessage()).showAndWait();
            }
        });

        Button annulerBtn = new Button("Annuler");
        annulerBtn.setOnAction(ev -> fenetre.close());

        HBox actions = new HBox(10, enregistrerBtn, annulerBtn);
        actions.setStyle("-fx-alignment: center-right;");

        VBox root = new VBox(10,
            new Label("Ajouter un document :"),
            new Label("Type :"), typeCombo,
            new Label("Titre :"), titreField,
            new Label("Auteur :"), auteurField,
            new Label("Genre :"), genreField,
            champsSpecifiques,
            actions
        );
        root.setStyle("-fx-padding: 15; -fx-alignment: center-left;");

        Scene scene = new Scene(root, 600, 600);
        
        // Charge le CSS dans la nouvelle fenêtre
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS non trouvé sur le classpath !");
        }


        fenetre.setScene(scene);
        fenetre.show();
    }

    
    private void afficherFenetreDocuments() {
        Stage fenetre = new Stage();
        fenetre.setTitle("Liste des documents");

        List<Document> listeDocs = manager.getDocuments();
        ObservableList<Document> documents = FXCollections.observableArrayList(listeDocs);

        TableView<Document> table = new TableView<>(documents);

        // 1. Colonne Type
        TableColumn<Document, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(cellData -> {
            Document doc = cellData.getValue();
            
            String type = (doc instanceof Livre) ? "📚 Livre" : "📰 Mag.";
            return new SimpleStringProperty(type);
        });

        // 2. Colonne Titre
        TableColumn<Document, String> colTitre = new TableColumn<>("Titre");
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titreDocument"));

        // 3. Colonne Auteur
        TableColumn<Document, String> colAuteur = new TableColumn<>("Auteur");
        colAuteur.setCellValueFactory(new PropertyValueFactory<>("auteur"));

        // 4. Colonne Genre
        TableColumn<Document, String> colGenre = new TableColumn<>("Genre");
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        
        TableColumn<Document, String> colDispo = new TableColumn<>("Dispo.");
        colDispo.setCellValueFactory(cellData -> {
            boolean estDispo = cellData.getValue().getStatut();
            // Si true -> Vert/Coche, Sinon -> Rouge/Croix
            return new SimpleStringProperty(estDispo ? "✅" : "❌");
        });
        // Centrer le texte pour que ce soit joli
        colDispo.setStyle("-fx-alignment: CENTER;");
        
        // 5. Colonne Détails 
        TableColumn<Document, String> colDetails = new TableColumn<>("Détails");
        colDetails.setCellValueFactory(cellData -> {
            Document d = cellData.getValue();
            if (d instanceof Livre) {
                Livre l = (Livre) d;
                // Affiche ISBN et Pages si disponibles
                String info = "";
                if (l.getIsbn() != null && !l.getIsbn().isEmpty()) info += "ISBN: " + l.getIsbn() + " ";
                if (l.getNbPages() != null && l.getNbPages() > 0) info += "(" + l.getNbPages() + " p.)";
                return new SimpleStringProperty(info);
            } else if (d instanceof Magazine) {
                Magazine m = (Magazine) d;
                List<String> details = new ArrayList<>();                
                // On ajoute à la liste seulement si > 0
                if (m.getNumero() > 0) {
                    details.add("N°" + m.getNumero());
                }
                if (m.getPeriodicite() > 0) {
                    details.add("Périodicité: " + m.getPeriodicite() + "j");
                }                
                if (details.isEmpty()) {
                    return new SimpleStringProperty(""); // Affiche vide 
                } else {
                    return new SimpleStringProperty(String.join(" - ", details));
                }
            }
            return new SimpleStringProperty("");
        });

        // On ajoute toutes les colonnes
        table.getColumns().addAll(colType, colTitre, colAuteur, colGenre, colDispo, colDetails);
        
        // Ajustement de la largeur
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colDetails.setMinWidth(150); // Donne un peu de place aux détails

     // 3. Mise en page "Carte" 
        Label titre = new Label("📚 Catalogue Complet");
        titre.getStyleClass().add("window-title");

        // La carte blanche
        VBox card = new VBox(15, titre, table);
        card.getStyleClass().add("content-card"); // applique le style
        card.setAlignment(Pos.CENTER);
        
        // Le conteneur de fond 
        StackPane root = new StackPane(card);
        root.setPadding(new Insets(30)); // Marge autour de la carte

        // 4. Création de la Scène avec le CSS
        Scene scene = new Scene(root, 950, 600);
        
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS non trouvé sur le classpath !");
        }


        fenetre.setScene(scene);
        fenetre.show();
    }
    
    private void afficherFenetreAdherents() {
        Stage fenetre = new Stage();
        fenetre.setTitle("Liste des adhérents");

        // Récupère la liste depuis ton manager
        List<Adherent> liste = manager.getAdherents();
        ObservableList<Adherent> adherents = FXCollections.observableArrayList(liste);

        // TableView
        TableView<Adherent> table = new TableView<>(adherents);

        // Colonnes
        TableColumn<Adherent, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<Adherent, String> colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));

        TableColumn<Adherent, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        
        table.getColumns().addAll(colNom, colPrenom, colEmail);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
  

        VBox root = new VBox(10, new Label("👥 Adhérents enregistrés :"), table);
        root.setStyle("-fx-padding: 15; -fx-alignment: center;");
        
        Scene scene = new Scene(root, 900, 500);
        
        //Charge le CSS dans la nouvelle fenêtre
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS non trouvé sur le classpath !");
        }


        fenetre.setScene(scene);
        fenetre.show();
    }
    
    
    private void afficherFenetreAjoutAdherent() {
        Stage fenetre = new Stage();
        fenetre.setTitle("Ajouter un adhérent");

        // Champs de saisie
        TextField nomField = new TextField();
        nomField.setPromptText("Nom");

        TextField prenomField = new TextField();
        prenomField.setPromptText("Prénom");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        // Boutons
        Button enregistrerBtn = new Button("Enregistrer");
        Button annulerBtn = new Button("Annuler");

        enregistrerBtn.setOnAction(ev -> {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String email = emailField.getText().trim();

            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Nom, prénom et email sont obligatoires.").showAndWait();
                return;
            }

            try {
                // Création de l'objet
                Adherent a = new Adherent(nom, prenom, email);
                
                // Appel de la méthode (qui ne renvoie rien si tout va bien)
                manager.ajouterAdherent(a);

                // Si on arrive ici, c'est qu'aucune exception n'a été levée -> SUCCÈS
                new Alert(Alert.AlertType.INFORMATION, "Adhérent ajouté avec succès !").showAndWait();
                fenetre.close();

            } catch (BibliothequeException ex) {
                // Erreur(Doublon, problème BDD...)
                new Alert(Alert.AlertType.WARNING, "Impossible d'ajouter l'adhérent :\n" + ex.getMessage()).showAndWait();
                
            } catch (IllegalArgumentException ex) {
                // Erreur de validation (ex: null)
                new Alert(Alert.AlertType.ERROR, "Erreur de données : " + ex.getMessage()).showAndWait();
                
            } catch (Exception ex) {
                // Autre erreur imprévue
                new Alert(Alert.AlertType.ERROR, "Erreur inattendue : " + ex.getMessage()).showAndWait();
            }
        });

        annulerBtn.setOnAction(ev -> fenetre.close());

        // Mise en page
        HBox actions = new HBox(10, enregistrerBtn, annulerBtn);
        actions.setStyle("-fx-alignment: center-right;");

        VBox root = new VBox(10,
            new Label("Ajouter un adhérent :"),
            new Label("Nom :"), nomField,
            new Label("Prénom :"), prenomField,
            new Label("Email :"), emailField,
            actions
        );
        root.setStyle("-fx-padding: 16; -fx-alignment: center-left;");

        Scene scene = new Scene(root, 600, 400);
        
        
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS non trouvé sur le classpath !");
        }


        fenetre.setScene(scene);
        fenetre.show();
    }

    
    
    private void afficherFenetreEmprunterDocument() {
        Stage fenetre = new Stage();
        fenetre.setTitle("📖 Emprunter un document");

        // Champs de saisie
        TextField emailField = new TextField();
        emailField.setPromptText("Email de l'adhérent");

        TextField titreField = new TextField();
        titreField.setPromptText("Titre du document");
        
        TextField auteurField = new TextField();
        auteurField.setPromptText("Auteur du document");

        Button emprunterBtn = new Button("Emprunter");
        Button annulerBtn = new Button("Annuler");

        emprunterBtn.setOnAction(ev -> {
            String email = emailField.getText().trim();
            String titre = titreField.getText().trim();
            String auteur = auteurField.getText().trim();

            // On verifie les champs vides
            if (email.isEmpty() || titre.isEmpty() || auteur.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Email et Titre sont obligatoires.").showAndWait();
                return;
            }

            // On recherche l'adhérent
            Adherent adherentTrouve = null;
            for (Adherent a : manager.getAdherents()) {
                if (a.getEmail().equalsIgnoreCase(email)) {
                    adherentTrouve = a;
                    break;
                }
            }

            if (adherentTrouve == null) {
                new Alert(Alert.AlertType.ERROR, "Aucun adhérent trouvé avec cet email.").showAndWait();
                return;
            }

            // On recherche le document
            Document documentTrouve = null;
            for (Document d : manager.getDocuments()) {
                if (d.getTitreDocument().equalsIgnoreCase(titre)
                    && d.getAuteur().equalsIgnoreCase(auteur)) {
                    documentTrouve = d;
                    break;
                }
            }

            if (documentTrouve == null) {
                new Alert(Alert.AlertType.ERROR, "Aucun document trouvé avec ce titre.").showAndWait();
                return;
            }
            // on appelle emprunt dans bibliotheque
            try {
            	// On tente l'emprunt
                manager.emprunterDocument(documentTrouve, adherentTrouve);
                // Si ça passe -> SUCCÈS
                new Alert(Alert.AlertType.INFORMATION, 
                    "Emprunt enregistré avec succès pour " + adherentTrouve.getPrenom()
                ).showAndWait();
                
                fenetre.close();
            } catch (BibliothequeException ex) {
                // On affiche un avertissement clair si Erreur
                new Alert(Alert.AlertType.WARNING, "Emprunt impossible :\n" + ex.getMessage()).showAndWait();                
            } catch (IllegalArgumentException ex) {
                // Erreur de données (null)
                new Alert(Alert.AlertType.ERROR, "Erreur interne : " + ex.getMessage()).showAndWait();              
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Erreur système : " + ex.getMessage()).showAndWait();
            }
        });

        annulerBtn.setOnAction(ev -> fenetre.close());

        // Mise en page
        HBox boutons = new HBox(10, emprunterBtn, annulerBtn);
        boutons.setStyle("-fx-alignment: center-right;");

        VBox root = new VBox(10,
            new Label("📚 Emprunter un document :"),
            new Label("Email de l'adhérent :"), emailField,
            new Label("Titre du document :"), titreField,
            new Label("Auteur du document :"), auteurField,
            boutons
        );

        root.setStyle("-fx-padding: 20; -fx-alignment: center-left;");

        Scene scene = new Scene(root, 600, 400);
        
         
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS non trouvé sur le classpath !");
        }


        fenetre.setScene(scene);
        fenetre.show();
    }

    
 

    private void afficherFenetreRetourDocument() {
        Stage fenetre = new Stage();
        fenetre.setTitle("📗 Retourner un document");

        TextField emailField = new TextField();
        emailField.setPromptText("Email de l'adhérent");

        TextField titreField = new TextField();
        titreField.setPromptText("Titre du document");
        
        TextField auteurField = new TextField();
        auteurField.setPromptText("Auteur du document");

        // Checkbox pour règlement immédiat (désactivée par défaut)
        CheckBox payeCheck = new CheckBox("Amende réglée immédiatement ?");
        payeCheck.setDisable(true); 

        Button verifierBtn = new Button("Vérifier le retard");
        Button validerRetourBtn = new Button("Valider le retour");
        validerRetourBtn.setDisable(true); // On ne peut valider qu'après vérification

        Label infoLabel = new Label(); // Pour afficher le montant de l'amende
        infoLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        // 1. Action de vérification 
        verifierBtn.setOnAction(ev -> {
            String email = emailField.getText().trim();
            String titre = titreField.getText().trim();
            String auteur = auteurField.getText().trim();

            if (email.isEmpty() || titre.isEmpty() || auteur.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Remplissez tous les champs.").showAndWait();
                return;
            }

            // On cherche les objets (comme avant)
            Adherent adh = manager.chercherAdherentParEmail(email);
            Document doc = manager.chercherDocumentParTitreEtAuteur(titre, auteur);

            if (adh == null || doc == null) {
                infoLabel.setText("Document ou adhérent introuvable.");
                return;
            }

            // On cherche l'emprunt
            Emprunt emp = null;
            for (Emprunt e : manager.getEmprunts()) {
                if (e.getDocument() == doc && e.getAdherent() == adh && e.getDateRetourReelle() == null) {
                    emp = e;
                    break;
                }
            }

            if (emp == null) {
                infoLabel.setText("Aucun emprunt en cours trouvé.");
                return;
            }

            // Calcul de l'amende
            long joursRetard = java.time.temporal.ChronoUnit.DAYS.between(emp.getDateRetourPrevue(), java.time.LocalDate.now());
            
            if (joursRetard > 0) {
                double montant = joursRetard * 0.50;
                infoLabel.setText("RETARD : " + joursRetard + " jours. Amende : " + String.format("%.2f", montant) + " €");
                payeCheck.setDisable(false); // On autorise à cocher la case "Payé"
                payeCheck.setSelected(false);
            } else {
                infoLabel.setText("✅ Retour dans les temps. Aucune pénalité.");
                infoLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                payeCheck.setDisable(true);
            }
            
            validerRetourBtn.setDisable(false); // Maintenant on peut valider
        });

        // 2. Action finale : Retour réel
        validerRetourBtn.setOnAction(ev -> {
            String email = emailField.getText().trim();
            String titre = titreField.getText().trim();
            String auteur = auteurField.getText().trim();
            
            Adherent adh = manager.chercherAdherentParEmail(email);
            Document doc = manager.chercherDocumentParTitreEtAuteur(titre, auteur);

            // Appel au manager pour le retour
            String resultat = manager.retourDocument(doc, adh);

          
            // Si c'est en retard MAIS que la case "Réglée" est cochée
            if (!payeCheck.isDisabled() && payeCheck.isSelected()) {
                try {
                    // On demande directement au manager de passer la pénalité à 'false'
                    manager.modifierAdherent(adh, null, null, null, false);
                    
                    // Si ça marche, on ajoute le message de succès
                    resultat += "\n💰 L'amende a été réglée sur place. Adhérent non bloqué.";
                    
                } catch (BibliothequeException ex) {
                    // Si ça rate on prévient l'utilisateur dans le message final
                    resultat += "\n⚠️ Attention : Impossible d'enregistrer le paiement (" + ex.getMessage() + ")";
                }
            }

            new Alert(Alert.AlertType.INFORMATION, resultat).showAndWait();
            fenetre.close();
        });

        VBox root = new VBox(15,
            new Label("📗 Retourner un document"),
            new Label("Email :"), emailField,
            new Label("Titre :"), titreField,
            new Label("Auteur :"), auteurField,
            verifierBtn,
            infoLabel,
            payeCheck,
            validerRetourBtn
        );
        root.setStyle("-fx-padding: 20; -fx-alignment: center-left;");
        
        Scene scene = new Scene(root, 700, 550);
        
        // Charge le CSS dans la nouvelle fenêtre
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS non trouvé sur le classpath !");
        }
        fenetre.setScene(scene);
        fenetre.show();
    }
    
    
    
    private void afficherFenetreEmpruntsEnCours() {
        Stage fenetre = new Stage();
        fenetre.setTitle("📚 Emprunts en cours");

        // Récupère les emprunts non rendus
        List<Emprunt> liste = new ArrayList<>();
        for (Emprunt e : manager.getEmprunts()) {
            if (e != null && e.getDateRetourReelle() == null) {
                liste.add(e);
            }
        }

        // Si aucun emprunt en cours -> message
        if (liste.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Aucun emprunt en cours").showAndWait();
            return;
        }

        ObservableList<Emprunt> emprunts = FXCollections.observableArrayList(liste);

        // TableView
        TableView<Emprunt> table = new TableView<>(emprunts);

        // Colonnes
        TableColumn<Emprunt, String> colTitre = new TableColumn<>("Titre");
        colTitre.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getDocument().getTitreDocument()));

        TableColumn<Emprunt, String> colAuteur = new TableColumn<>("Auteur");
        colAuteur.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getDocument().getAuteur()));

        TableColumn<Emprunt, String> colAdh = new TableColumn<>("Adhérent");
        colAdh.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                cellData.getValue().getAdherent().getEmail()
                
            ));

        TableColumn<Emprunt, LocalDate> colDateEmp = new TableColumn<>("Date emprunt");
        colDateEmp.setCellValueFactory(new PropertyValueFactory<>("dateEmprunt"));

        TableColumn<Emprunt, LocalDate> colDatePrev = new TableColumn<>("Date retour prévue");
        colDatePrev.setCellValueFactory(new PropertyValueFactory<>("dateRetourPrevue"));

        //Ajout des colonnes à la table 
        table.getColumns().addAll(colTitre, colAuteur, colAdh, colDateEmp, colDatePrev);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //Style et présentation 
        Label titre = new Label("📚 Emprunts actuellement en cours");
        titre.getStyleClass().add("modal-title"); // 
        
        VBox root = new VBox(12, titre, table);
        root.setStyle("-fx-padding: 15; -fx-alignment: center;");

        Scene scene = new Scene(root, 900, 500);
        
        // Charge le CSS dans la nouvelle fenêtre
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS non trouvé sur le classpath !");
        }
        fenetre.setScene(scene);
        fenetre.show();
    }

    
    private void afficherFenetreRetards() {
        Stage fenetre = new Stage();
        fenetre.setTitle("Emprunts en retard");

        LocalDate today = LocalDate.now();
        List<Emprunt> enRetard = new ArrayList<>();

        
        for (Emprunt e : manager.getEmprunts()) {
            if (e != null 
                && e.getDateRetourReelle() == null 
                && e.getDateRetourPrevue().isBefore(today)) {
                enRetard.add(e);
            }
        }
        
        // Si aucun emprunt en retard -> petit message et on sort
        if (enRetard.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Aucun emprunt en retard").showAndWait();
            return;
        }

        ObservableList<Emprunt> data = FXCollections.observableArrayList(enRetard);
        TableView<Emprunt> table = new TableView<>(data);

        // Colonnes
        TableColumn<Emprunt, String> colDoc = new TableColumn<>("Document");
        colDoc.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getDocument().getTitreDocument()));

        TableColumn<Emprunt, String> colAdh = new TableColumn<>("Adhérent");
        colAdh.setCellValueFactory(cd ->
            new SimpleStringProperty(
                cd.getValue().getAdherent().getEmail()
            ));

        TableColumn<Emprunt, LocalDate> colPrev = new TableColumn<>("Date retour prévue");
        colPrev.setCellValueFactory(new PropertyValueFactory<>("dateRetourPrevue"));

        // Jours de retard (calcul directe)
        TableColumn<Emprunt, String> colRetard = new TableColumn<>("Jours de retard");
        colRetard.setCellValueFactory(cd -> {
            LocalDate prev = cd.getValue().getDateRetourPrevue();
            long jours = (prev == null) ? 0 : ChronoUnit.DAYS.between(prev, LocalDate.now());
            if (jours < 0) jours = 0;
            return new SimpleStringProperty(String.valueOf(jours));
        });

        table.getColumns().addAll(colDoc, colAdh, colPrev, colRetard);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox root = new VBox(10, new Label("⏰ Liste des emprunts en retard :"), table);
        root.setStyle("-fx-padding: 15; -fx-alignment: center;");

        Scene scene = new Scene(root, 900, 500);
        
        // Charge le CSS dans la nouvelle fenêtre
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS non trouvé sur le classpath !");
        }
        fenetre.setScene(scene);
        fenetre.show();
    }
    
    private void afficherFenetreHistoriqueAdherent() {
        Stage fenetreSaisie = new Stage();
        fenetreSaisie.setTitle("Historique d’un adhérent");

        TextField emailField = new TextField();
        emailField.setPromptText("Email de l’adhérent");

        Button validerBtn = new Button("Afficher l’historique");
        Button annulerBtn = new Button("Annuler");

        validerBtn.setOnAction(ev -> {
            String email = emailField.getText().trim();

            if (email.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Veuillez saisir un email.").showAndWait();
                return;
            }

            Adherent adherentTrouve = null;
            for (Adherent a : manager.getAdherents()) {
                if (a.getEmail().equalsIgnoreCase(email)) {
                    adherentTrouve = a;
                    break;
                }
            }

            if (adherentTrouve == null) {
                new Alert(Alert.AlertType.ERROR, "Aucun adhérent trouvé avec cet email.").showAndWait();
                return;
            }

            // Récupère l’historique depuis la base via BibliothequeManager
            List<Emprunt> historique = manager.getDb().getHistoriqueAdherent(email);

            if (historique == null || historique.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "Aucun emprunt trouvé pour cet adhérent.").showAndWait();
                return;
            }

            fenetreSaisie.close();
            afficherTableHistorique(adherentTrouve, historique);
        });

        annulerBtn.setOnAction(ev -> fenetreSaisie.close());

        HBox actions = new HBox(10, validerBtn, annulerBtn);
        actions.setStyle("-fx-alignment: center;");

        VBox root = new VBox(10,
            new Label("📧 Entrez l’email de l’adhérent :"),
            emailField,
            actions
        );
        root.setStyle("-fx-padding: 15; -fx-alignment: center;");

        Scene scene = new Scene(root, 600, 400);
        
        // Charge le CSS dans la nouvelle fenêtre
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS non trouvé sur le classpath !");
        }
        fenetreSaisie.setScene(scene);
        fenetreSaisie.show();
    }
    
    private void afficherTableHistorique(Adherent adherent, List<Emprunt> historique) {
        Stage fenetre = new Stage();
        fenetre.setTitle("Historique de " + adherent.getPrenom() + " " + adherent.getNom());

        ObservableList<Emprunt> data = FXCollections.observableArrayList(historique);
        TableView<Emprunt> table = new TableView<>(data);

        TableColumn<Emprunt, String> colDoc = new TableColumn<>("Document");
        colDoc.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getDocument().getTitreDocument()));

        TableColumn<Emprunt, LocalDate> colEmp = new TableColumn<>("Date emprunt");
        colEmp.setCellValueFactory(new PropertyValueFactory<>("dateEmprunt"));

        TableColumn<Emprunt, LocalDate> colPrev = new TableColumn<>("Date retour prévue");
        colPrev.setCellValueFactory(new PropertyValueFactory<>("dateRetourPrevue"));

        TableColumn<Emprunt, LocalDate> colReelle = new TableColumn<>("Date retour réelle");
        colReelle.setCellValueFactory(new PropertyValueFactory<>("dateRetourReelle"));

        table.getColumns().addAll(colDoc, colEmp, colPrev, colReelle);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox root = new VBox(10,
            new Label("Historique des emprunts de " + adherent.getPrenom() + " " + adherent.getNom() + " :"),
            table
        );
        root.setStyle("-fx-padding: 15; -fx-alignment: center;");

        Scene scene = new Scene(root, 900, 500);
        
        // Charge le CSS dans la nouvelle fenêtre
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS non trouvé sur le classpath !");
        }
        fenetre.setScene(scene);
        fenetre.show();
    }
    
    private void afficherFenetreSupprimerAdherent() {
        Stage fenetre = new Stage();
        fenetre.setTitle("Supprimer un adhérent");

        TextField emailField = new TextField();
        emailField.setPromptText("Email de l'adhérent à supprimer");

        Button supprimerBtn = new Button("Supprimer");
        Button annulerBtn = new Button("Annuler");

        supprimerBtn.setOnAction(ev -> {
            String email = emailField.getText().trim();

            if (email.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Veuillez saisir un email.").showAndWait();
                return;
            }

           
            Adherent adherentTrouve = null;
            for (Adherent a : manager.getAdherents()) {
                if (a.getEmail().equalsIgnoreCase(email)) {
                    adherentTrouve = a;
                    break;
                }
            }

            if (adherentTrouve == null) {
                new Alert(Alert.AlertType.ERROR, "Aucun adhérent trouvé avec cet email.").showAndWait();
                return;
            }

            // Vérifier les emprunts en cours avant suppression
            boolean aDesEmprunts = false;
            for (Emprunt e : manager.getEmprunts()) {
                if (e.getAdherent() == adherentTrouve && e.getDateRetourReelle() == null) {
                    aDesEmprunts = true;
                    break;
                }
            }

            if (aDesEmprunts) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Suppression impossible");
                alert.setHeaderText("Impossible de supprimer cet adhérent");
                alert.setContentText("Cet adhérent a des emprunts en cours !");
                                     
                
                // Permettre au texte de s'afficher correctement
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                
                alert.showAndWait();
                return;
            }

            // Confirmation avant suppression
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer définitivement " + adherentTrouve.getPrenom() + " " + adherentTrouve.getNom() + " ?");
            confirmation.setHeaderText("Confirmation requise");
            
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                manager.supprimerAdherent(adherentTrouve);
                new Alert(Alert.AlertType.INFORMATION, "Adhérent supprimé avec succès.").showAndWait();
                fenetre.close();
            }
        });

        annulerBtn.setOnAction(ev -> fenetre.close());

        HBox boutons = new HBox(10, supprimerBtn, annulerBtn);
        boutons.setStyle("-fx-alignment: center-right;");

        VBox root = new VBox(10,
            new Label("🗑️ Supprimer un adhérent :"),
            new Label("Email de l'adhérent :"), emailField,
            boutons
        );
        root.setStyle("-fx-padding: 15; -fx-alignment: center-left;");
        
        Scene scene = new Scene(root, 600, 400);
        
        // Charge le CSS dans la nouvelle fenêtre
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS non trouvé sur le classpath !");
        }


        fenetre.setScene(scene);
        fenetre.show();
    }
    
    private void afficherFenetreSupprimerDocument() {
        Stage fenetre = new Stage();
        fenetre.setTitle("Supprimer un document");

        TextField titreField = new TextField();
        titreField.setPromptText("Titre du document");

        TextField auteurField = new TextField();
        auteurField.setPromptText("Auteur du document");

        Button supprimerBtn = new Button("Supprimer");
        Button annulerBtn = new Button("Annuler");

        supprimerBtn.setOnAction(ev -> {
            String titre = titreField.getText().trim();
            String auteur = auteurField.getText().trim();

            if (titre.isEmpty() || auteur.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Veuillez saisir le titre et l’auteur du document.").showAndWait();
                return;
            }

            // recherche du document
            Document documentTrouve = null;
            for (Document d : manager.getDocuments()) {
                if (d.getTitreDocument().equalsIgnoreCase(titre)
                        && d.getAuteur().equalsIgnoreCase(auteur)) {
                    documentTrouve = d;
                    break;
                }
            }

            if (documentTrouve == null) {
                new Alert(Alert.AlertType.ERROR, "Aucun document trouvé avec ce titre et auteur.").showAndWait();
                return;
            }
                 

            // Vérifier qu’il n’est pas emprunté
            if (!documentTrouve.getStatut()) {
                new Alert(Alert.AlertType.WARNING,
                    "Impossible de supprimer : le document est emprunté."
                ).showAndWait();
                return;
            }

            // Demande de confirmation
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer définitivement le document '" + documentTrouve.getTitreDocument() + "' de " + documentTrouve.getAuteur() + " ?");
            confirmation.setHeaderText("Confirmation requise");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                manager.supprimerDocument(documentTrouve);
                new Alert(Alert.AlertType.INFORMATION,
                    "Document supprimé avec succès."
                ).showAndWait();
                fenetre.close();
            }
        });

        annulerBtn.setOnAction(ev -> fenetre.close());

        HBox boutons = new HBox(10, supprimerBtn, annulerBtn);
        boutons.setStyle("-fx-alignment: center-right;");

        VBox root = new VBox(10,
            new Label("🗑️ Supprimer un document :"),
            new Label("Titre :"), titreField,
            new Label("Auteur :"), auteurField,
            boutons
        );
        root.setStyle("-fx-padding: 15; -fx-alignment: center-left;");

        Scene scene = new Scene(root, 600, 400);
        
        // Charge le CSS dans la nouvelle fenêtre
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS non trouvé sur le classpath !");
        }


        fenetre.setScene(scene);
        fenetre.show();
    }

    
    private void afficherFenetreModifierAdherent() {
        Stage fenetre = new Stage();
        fenetre.setTitle("Modifier un adhérent");

        TextField emailField = new TextField();
        emailField.setPromptText("Email de l’adhérent à modifier");

        Button rechercherBtn = new Button("Rechercher");
        Button annulerBtn = new Button("Annuler");

        // Layout principal
        VBox root = new VBox(10,
            new Label("✏️ Modifier un adhérent :"),
            new Label("Email :"), emailField,
            new HBox(10, rechercherBtn, annulerBtn)
        );
        root.setStyle("-fx-padding: 15; -fx-alignment: center-left;");
        
        Scene scene = new Scene(root, 600, 400);
        
        // Charge le CSS dans la nouvelle fenêtre
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS non trouvé sur le classpath !");
        }


        fenetre.setScene(scene);
        fenetre.show();

        // Action "Annuler"
        annulerBtn.setOnAction(ev -> fenetre.close());

        // Action "Rechercher"
        rechercherBtn.setOnAction(ev -> {
            String email = emailField.getText().trim();
            if (email.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Veuillez entrer un email.").showAndWait();
                return;
            }

            Adherent adherentTrouve = null;
            for (Adherent a : manager.getAdherents()) {
                if (a.getEmail().equalsIgnoreCase(email)) {
                    adherentTrouve = a;
                    break;
                }
            }

            if (adherentTrouve == null) {
                new Alert(Alert.AlertType.ERROR, "Aucun adhérent trouvé avec cet email.").showAndWait();
                return;
            }

            // Ouvrir la fenêtre de modification
            fenetre.close();
            afficherFenetreEditionAdherent(adherentTrouve);
        });
    }
    
    private void afficherFenetreEditionAdherent(Adherent adherent) {
        Stage fenetre = new Stage();
        fenetre.setTitle("Gestion : " + adherent.getPrenom() + " " + adherent.getNom());

        
        TextField nomField = new TextField(adherent.getNom());
        TextField prenomField = new TextField(adherent.getPrenom());
        TextField emailField = new TextField(adherent.getEmail());
        
        
        // 1. L'indicateur visuel
        Label statusLabel = new Label();
        // On donne une taille fixe minimale pour éviter que ça bouge
        statusLabel.setMinWidth(100); 
        statusLabel.setAlignment(Pos.CENTER);
        
        // 2. Le bouton d'action
        Button actionBtn = new Button();
        
      
        
        // 3. La logique de mise à jour (Runnable)
        Runnable updateUI = () -> {
            if (adherent.getStatut()) {
                // Cas : Il est PÉNALISÉ 
                statusLabel.setText("Pénalisé");
                statusLabel.setStyle("-fx-text-fill: white; -fx-background-color: #991b1b; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 4;");
                
                actionBtn.setText("Lever la sanction");
                actionBtn.setStyle("-fx-background-color: #15803d; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;"); 
                
            } else {
                // Cas : Il est EN RÈGLE
                statusLabel.setText("En règle");
                statusLabel.setStyle("-fx-text-fill: white; -fx-background-color: #15803d; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 4;");
                
                actionBtn.setText("Appliquer une pénalité");
                actionBtn.setStyle("-fx-background-color: #991b1b; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;"); 
            }
        };

        // Appel initial pour afficher le bon statut des l'ouverture
        updateUI.run(); 

        // 4. Action du bouton : On inverse le statut et on met à jour
        actionBtn.setOnAction(e -> {
            boolean valeurDesiree = !adherent.getStatut();
            
            try {
                // Appel de la méthode (ne renvoie rien si succès)
                manager.modifierAdherent(adherent, null, null, null, valeurDesiree);
                
                // Succès : on met à jour l'affichage
                updateUI.run();
                
                System.out.println("Statut modifié avec succès.");

            } catch (BibliothequeException ex) {
                // Erreur 
                new Alert(Alert.AlertType.ERROR, "Erreur : " + ex.getMessage()).showAndWait();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Erreur inattendue : " + ex.getMessage()).showAndWait();
            }
        });
          
        //Boutons du bas (Sauvegarder les noms/emails)
        Button enregistrerBtn = new Button("Enregistrer modifications");
        Button annulerBtn = new Button("Fermer");

        enregistrerBtn.setOnAction(ev -> {
        	String nNom = nomField.getText().trim();
            String nPrenom = prenomField.getText().trim();
            String nEmail = emailField.getText().trim();

            try {
                // On tente la modification
                manager.modifierAdherent(adherent, nNom, nPrenom, nEmail, null);
                
                // Si on arrive ici, c'est un ok (car pas d'exception)
                new Alert(Alert.AlertType.INFORMATION, "Modifications enregistrées avec succès !").showAndWait();
                fenetre.close();

            } catch (BibliothequeException ex) {
                // On gère les deux types de messages ici :
                
                if (ex.getMessage().contains("Aucune modification")) {
                    // Cas "Rien n'a changé" : On ferme juste la fenêtre 
                    fenetre.close(); 
                } else {
                    // Cas "Erreur" (Doublon email, etc.) : On affiche une alerte rouge
                    new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
                }

            } catch (Exception ex) {
                // Autres erreurs graves
                new Alert(Alert.AlertType.ERROR, "Erreur système : " + ex.getMessage()).showAndWait();
            }
        });

        annulerBtn.setOnAction(ev -> fenetre.close());

        HBox boutonsBas = new HBox(10, enregistrerBtn, annulerBtn);
        boutonsBas.setAlignment(Pos.CENTER_RIGHT);
        
        HBox zoneStatut = new HBox(30, new Label("Statut actuel :"), statusLabel, actionBtn);
        zoneStatut.setAlignment(Pos.CENTER_LEFT);
        zoneStatut.setStyle("-fx-padding: 10; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");

        
        VBox root = new VBox(15,
            new Label("✏️ Modifier les informations :"),
            new Label("Nom :"), nomField,
            new Label("Prénom :"), prenomField,
            new Label("Email :"), emailField,
            new Label("Gestion des accès :"),
            zoneStatut, 
     
            boutonsBas
        );
        root.setStyle("-fx-padding: 20; -fx-alignment: center-left;");
        
        Scene scene = new Scene(root, 900, 500);
        
        // Charge le CSS dans la nouvelle fenêtre
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS non trouvé sur le classpath !");
        }


        fenetre.setScene(scene);
        fenetre.show();
    }
    
    private void afficherFenetreModifierDocument() {
        Stage fenetre = new Stage();
        fenetre.setTitle("Modifier un document");

        TextField titreField = new TextField();
        titreField.setPromptText("Titre du document");

        TextField auteurField = new TextField();
        auteurField.setPromptText("Auteur du document");

        Button rechercherBtn = new Button("Rechercher");
        Button annulerBtn = new Button("Annuler");

        rechercherBtn.setOnAction(ev -> {
            String titre = titreField.getText().trim();
            String auteur = auteurField.getText().trim();

            if (titre.isEmpty() || auteur.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Veuillez saisir le titre et l’auteur.").showAndWait();
                return;
            }

            Document docTrouve = null;
            for (Document d : manager.getDocuments()) {
                if (d.getTitreDocument().equalsIgnoreCase(titre)
                        && d.getAuteur().equalsIgnoreCase(auteur)) {
                    docTrouve = d;
                    break;
                }
            }

            if (docTrouve == null) {
                new Alert(Alert.AlertType.ERROR, "Aucun document trouvé avec ce titre et cet auteur.").showAndWait();
                return;
            }

            // Ouvrir la fenêtre d’édition
            fenetre.close();
            afficherFenetreEditionDocument(docTrouve);
        });

        annulerBtn.setOnAction(ev -> fenetre.close());

        HBox boutons = new HBox(10, rechercherBtn, annulerBtn);
        boutons.setStyle("-fx-alignment: center-right;");

        VBox root = new VBox(10,
            new Label("✏️ Modifier un document :"),
            new Label("Titre :"), titreField,
            new Label("Auteur :"), auteurField,
            boutons
        );
        root.setStyle("-fx-padding: 15; -fx-alignment: center-left;");

        Scene scene = new Scene(root, 600, 400);
        
        // Charge le CSS dans la nouvelle fenêtre
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS non trouvé sur le classpath !");
        }


        fenetre.setScene(scene);
        fenetre.show();
    }

 
    private void afficherFenetreEditionDocument(Document doc) {
        Stage fenetre = new Stage();
        fenetre.setTitle("Modifier : " + doc.getTitreDocument());

        
        TextField titreField = new TextField(doc.getTitreDocument() != null ? doc.getTitreDocument() : "");
        TextField auteurField = new TextField(doc.getAuteur() != null ? doc.getAuteur() : "");
        TextField genreField = new TextField(doc.getGenre() != null ? doc.getGenre() : ""); // 
        
        CheckBox dispoCheck = new CheckBox("Disponible ?");
        dispoCheck.setSelected(doc.getStatut());

        // 2. Les nouveaux champs 
        TextField champ1 = new TextField(); // Servira pour ISBN ou Numéro
        TextField champ2 = new TextField(); // Servira pour Pages ou Périodicité
        Label label1 = new Label();
        Label label2 = new Label();

        // 3. On remplit les cases selon le type
        if (doc instanceof Livre) {
            Livre l = (Livre) doc;
            label1.setText("ISBN :");
            champ1.setText(l.getIsbn() != null ? l.getIsbn() : ""); // ISBN optionnel

            label2.setText("Nombre de pages :");
            // On n'affiche que si > 0, sinon on laisse vide
            if (l.getNbPages() != null && l.getNbPages() > 0) {
                champ2.setText(String.valueOf(l.getNbPages()));
            }

        } else if (doc instanceof Magazine) {
            Magazine m = (Magazine) doc;
            label1.setText("Numéro :");
            if (m.getNumero() > 0) {
                champ1.setText(String.valueOf(m.getNumero()));
            } // sinon on laisse champ1 vide

            label2.setText("Périodicité :");
            if (m.getPeriodicite() > 0) {
                champ2.setText(String.valueOf(m.getPeriodicite()));
            } // sinon vide aussi
        }

        Button enregistrerBtn = new Button("Enregistrer");
        enregistrerBtn.setOnAction(ev -> {
            try {
                // Récupérer les infos de base
            	String t = (titreField.getText() != null) ? titreField.getText().trim() : "";
                String a = (auteurField.getText() != null) ? auteurField.getText().trim() : "";
                String g = (genreField.getText() != null) ? genreField.getText().trim() : "";
                boolean s = dispoCheck.isSelected();

                String val1 = (champ1.getText() != null) ? champ1.getText().trim() : "";
                String val2 = (champ2.getText() != null) ? champ2.getText().trim() : "";

                // Variables finales à envoyer
                String isbnFinal = null;
                Integer pagesFinal = null;
                Integer numeroFinal = null;
                Integer periodiciteFinal = null;

                // Conversion 
                if (doc instanceof Livre) {
                    isbnFinal = val1; // Le champ 1 est l'ISBN
                    if (!val2.isEmpty()) {
                        pagesFinal = Integer.parseInt(val2);
                        if (pagesFinal <= 0) throw new BibliothequeException("Le nombre de pages doit être positif !");
                    }
                } else {
                	if (!val1.isEmpty()) {
                        numeroFinal = Integer.parseInt(val1);
                        if (numeroFinal <= 0) throw new BibliothequeException("Le numéro du magazine doit être positif !");
                    }
                    if (!val2.isEmpty()) {
                        periodiciteFinal = Integer.parseInt(val2);
                        if (periodiciteFinal <= 0) throw new BibliothequeException("La périodicité doit être positive !");
                    }
                }

                // Appel de la méthode du Manager 
                manager.modifierDocument(doc, t, a, g, s, isbnFinal, pagesFinal, numeroFinal, periodiciteFinal);

                new Alert(Alert.AlertType.INFORMATION, "Document modifié avec succès !").showAndWait();
                fenetre.close();

            } catch (BibliothequeException ex) {
                 new Alert(Alert.AlertType.WARNING, "Attention : " + ex.getMessage()).showAndWait();
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Erreur de format : Vérifiez les nombres.").showAndWait();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Erreur inattendue : " + ex.getMessage()).showAndWait();
            }
        });

        // 4. Mise en page
        VBox root = new VBox(10,
            new Label("Titre"), titreField,
            new Label("Auteur"), auteurField,
            new Label("Genre"), genreField,
            label1, champ1,  // Affiche ISBN ou Numéro
            label2, champ2,  // Affiche Pages ou Périodicité
            dispoCheck,
            enregistrerBtn
        );
        root.setStyle("-fx-padding: 15; -fx-alignment: center-left;");
        
        Scene scene = new Scene(root, 900, 500);
        
        // Charge le CSS dans la nouvelle fenêtre
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS non trouvé sur le classpath !");
        }


        fenetre.setScene(scene);
        fenetre.show();
    }
    
    private void afficherFenetreRechercheGenre() {
        Stage fenetre = new Stage();
        fenetre.setTitle("Recherche de documents par genre");

        TextField genreField = new TextField();
        genreField.setPromptText("Exemple : Roman, Science-Fiction, Histoire...");

        Button rechercherBtn = new Button("Rechercher");
        Button annulerBtn = new Button("Annuler");

        rechercherBtn.setOnAction(ev -> {
            String genre = genreField.getText().trim();

            if (genre.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Veuillez saisir un genre.").showAndWait();
                return;
            }

            List<Document> resultats = manager.chercherDocumentParGenre(genre);
            

            if (resultats.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "Aucun document trouvé pour ce genre ❌").showAndWait();
                return;
            }

            fenetre.close();
            afficherTableDocuments("Résultats pour le genre : " + genre, resultats);
        });

        annulerBtn.setOnAction(ev -> fenetre.close());

        HBox boutons = new HBox(10, rechercherBtn, annulerBtn);
        boutons.setStyle("-fx-alignment: center-right;");

        VBox root = new VBox(10,
            new Label("🔎 Rechercher un document par genre :"),
            genreField,
            boutons
        );
        root.setStyle("-fx-padding: 16; -fx-alignment: center-left;");

        Scene scene = new Scene(root, 600, 400);
        
        // Charge le CSS dans la nouvelle fenêtre
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS non trouvé sur le classpath !");
        }


        fenetre.setScene(scene);
        fenetre.show();
    }
    
    private void afficherFenetreRechercheAuteur() {
        Stage fenetre = new Stage();
        fenetre.setTitle("Recherche de documents par auteur");

        TextField auteurField = new TextField();
        auteurField.setPromptText("Exemple : Victor Hugo, J.K. Rowling...");

        Button rechercherBtn = new Button("Rechercher");
        Button annulerBtn = new Button("Annuler");

        rechercherBtn.setOnAction(ev -> {
            String auteur = auteurField.getText().trim();

            if (auteur.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Veuillez saisir un auteur.").showAndWait();
                return;
            }
            
            List<Document> resultats = manager.chercherDocumentsParAuteur(auteur);

            if (resultats.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "Aucun document trouvé pour cet auteur").showAndWait();
                return;
            }

            fenetre.close();
            afficherTableDocuments("Résultats pour l’auteur : " + auteur, resultats);
        });

        annulerBtn.setOnAction(ev -> fenetre.close());

        HBox boutons = new HBox(10, rechercherBtn, annulerBtn);
        boutons.setStyle("-fx-alignment: center-right;");

        VBox root = new VBox(10,
            new Label("🔎 Rechercher un document par auteur :"),
            auteurField,
            boutons
        );
        root.setStyle("-fx-padding: 16; -fx-alignment: center-left;");

        Scene scene = new Scene(root, 600, 400);
        
        // Charge le CSS dans la nouvelle fenêtre
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS non trouvé sur le classpath !");
        }


        fenetre.setScene(scene);
        fenetre.show();
    }
    
    private void afficherFenetreRechercheTitre() {
        Stage fenetre = new Stage();
        fenetre.setTitle("🔎 Recherche par Titre");

        TextField titreField = new TextField();
        titreField.setPromptText("Exemple : Les Misérables, Harry Potter...");

        Button rechercherBtn = new Button("Rechercher");
        Button annulerBtn = new Button("Annuler");

        rechercherBtn.setOnAction(ev -> {
            String saisie = titreField.getText().trim();

            if (saisie.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Veuillez saisir un titre.").showAndWait();
                return;
            }

            // Appel au Manager
            List<Document> resultats = manager.chercherDocumentsParTitre(saisie);

            if (resultats.isEmpty()) {
                // Petite astuce : on essaye de chercher "qui contient" si la recherche exacte échoue
             
                for (Document d : manager.getDocuments()) {
                    if (d.getTitreDocument().toLowerCase().contains(saisie.toLowerCase())) {
                        if (!resultats.contains(d)) resultats.add(d);
                    }
                }
            }

            if (resultats.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "Aucun document trouvé avec ce titre ❌").showAndWait();
                return;
            }

            // Si on a trouvé, on ferme la petite fenêtre et on affiche le tableau
            fenetre.close();
            afficherTableDocuments("Résultats pour le titre : " + saisie, resultats);
        });

        // Action Annuler
        annulerBtn.setOnAction(ev -> fenetre.close());

        HBox boutons = new HBox(10, rechercherBtn, annulerBtn);
        boutons.setStyle("-fx-alignment: center-right;");

        VBox root = new VBox(15,
            new Label("🔎 Rechercher un document par titre :"),
            titreField,
            boutons
        );
        root.setStyle("-fx-padding: 20; -fx-alignment: center-left;");

        Scene scene = new Scene(root, 500, 200); 
        
        // Chargement du style
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS non trouvé sur le classpath !");
        }
        
        fenetre.setScene(scene);
        fenetre.show();
    }

    
    private void afficherTableDocuments(String titreFenetre, List<Document> documents) {
        Stage fenetre = new Stage();
        fenetre.setTitle(titreFenetre);

        ObservableList<Document> data = FXCollections.observableArrayList(documents);
        TableView<Document> table = new TableView<>(data);

        TableColumn<Document, String> colTitre = new TableColumn<>("Titre");
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titreDocument"));

        TableColumn<Document, String> colAuteur = new TableColumn<>("Auteur");
        colAuteur.setCellValueFactory(new PropertyValueFactory<>("auteur"));

        TableColumn<Document, String> colGenre = new TableColumn<>("Genre");
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));

        TableColumn<Document, String> colDispo = new TableColumn<>("Disponible");
        colDispo.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStatut() ? "✅" : "❌"));

        table.getColumns().addAll(colTitre, colAuteur, colGenre, colDispo);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox root = new VBox(10, new Label(titreFenetre), table);
        root.setStyle("-fx-padding: 15; -fx-alignment: center;");

        Scene scene = new Scene(root, 900, 500);
        
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS non trouvé sur le classpath !");
        }


        fenetre.setScene(scene);
        fenetre.show();
    }
    
    
    private void afficherFenetreStatutAdherent() {
        Stage fenetre = new Stage();
        fenetre.setTitle("Statut d’un adhérent ");

        TextField emailField = new TextField();
        emailField.setPromptText("Email de l’adhérent");

        Button rechercherBtn = new Button("Rechercher");
        Button fermerBtn = new Button("Fermer");

        Label resultatLabel = new Label();

        rechercherBtn.setOnAction(ev -> {
            String email = emailField.getText().trim();

            if (email.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Veuillez saisir un email.").showAndWait();
                return;
            }

            Adherent adherentTrouve = null;
            for (Adherent a : manager.getAdherents()) {
                if (a.getEmail().equalsIgnoreCase(email)) {
                    adherentTrouve = a;
                    break;
                }
            }

            if (adherentTrouve == null) {
                resultatLabel.setText("Aucun adhérent trouvé avec cet email.");
                resultatLabel.setStyle("-fx-text-fill: red;");
            } else {
                if (adherentTrouve.getStatut()) {
                    resultatLabel.setText("Adhérent pénalisé !");
                    resultatLabel.setStyle("-fx-text-fill: #b22222; -fx-font-weight: bold;");
                } else {
                    resultatLabel.setText("✅ Adhérent actif, aucun problème.");
                    resultatLabel.setStyle("-fx-text-fill: #2e8b57; -fx-font-weight: bold;");
                }
            }
        });

        fermerBtn.setOnAction(ev -> fenetre.close());

        HBox actions = new HBox(10, rechercherBtn, fermerBtn);
        actions.setAlignment(Pos.CENTER);

        VBox root = new VBox(15,
            new Label("🔍 Vérifier le statut d’un adhérent (entrez le email) :"),
            emailField,
            actions,
            resultatLabel
        );
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 600, 400);
        
        // Charge le CSS dans la nouvelle fenêtre
        var css = getClass().getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS non trouvé sur le classpath !");
        }
        fenetre.setScene(scene);
        fenetre.show();
    }
    
    


    public static void main(String[] args) {
        launch(args); // C'est cette ligne qui démarre JavaFX manuellement
    }



}


