Projet : SGEB - Système de Gestion des Emprunts de Bibliothèque


A. LANCEMENT DE L'APPLICATION: 
-----------------------------

1) LANCEMENT MANUEL :
--------------------
Ouvrir un terminal dans le dossier où se trouve le JAR et taper :

    java -jar Tsitsiev.jar



2) MÉTHODE 2 :
--------------------
1. Se placer dans le dossier contenant le JAR, par exemple :
   Tsitsiev/
2. Double-cliquer sur le fichier :
   Tsitsiev.jar




B. PRÉREQUIS:
------------

- Java 17 ou supérieur installé sur la machine
  (commande de test :  java -version)

- Aucune installation supplémentaire n'est nécessaire :
  - JavaFX est inclus dans le JAR (via Maven)
  - Le driver SQLite JDBC est inclus dans le JAR



C. COMPILATION (depuis les sources)
-----------------------------------

Le projet est configuré avec Maven.

1) Avec Maven en ligne de commande :
------------------------------------

1. Se placer dans le dossier du projet (là où se trouve pom.xml) :
   
       cd chemin/vers/le/projet

2. Lancer la commande :

       mvn clean package

3. Le JAR exécutable sera généré dans le dossier :

       target/bibliotheque-1.0-SNAPSHOT.jar

4. Lancer l'application avec :

       java -jar target/bibliotheque-1.0-SNAPSHOT.jar


2) Avec Eclipse (Maven intégré) :
---------------------------------

1. File > Import > Existing Maven Projects
2. Sélectionner le dossier contenant pom.xml
3. Clic droit sur le projet > Run As > Maven build...
4. Goals :  clean package
5. Le JAR sera généré dans le dossier target/



D. STRUCTURE DU PROJET:
----------------------

src/
└── main/
    ├── java/
    │   ├── app/
    │   │   ├── AppLauncher.java     (Point d'entrée du JAR)
    │   │   └── MainFX.java          (Interface JavaFX)
    │   │
    │   ├── model/
    │   │   ├── Adherent.java
    │   │   ├── Document.java        (Classe abstraite)
    │   │   ├── Livre.java           (Hérite de Document)
    │   │   ├── Magazine.java        (Hérite de Document)
    │   │   └── Emprunt.java
    │   │
    │   └── service/
    │       ├── BibliothequeManager.java  (Logique métier)
    │       ├── DatabaseManager.java      (Gestion SQLite)
    │       └── BibliothequeException.java
    │
    └── resources/
        └── style.css                (Feuille de style JavaFX)

Autres fichiers fournis :
- pom.xml           (configuration Maven)
- Biblio_Tsitsiev.jar   (JAR exécutable)
- user.pdf          (Documentation utilisateur, UML, modèles)
- README.txt        (Ce fichier)


Donc le rendu final est: 

Tsitsiev/
├─ src/
│  └─ main/
│     ├─ java/
│     │   ├─ app/
│     │   ├─ model/
│     │   └─ service/
│     └─ resources/
│         └─ style.css
├─ pom.xml
├─ Tsitsiev.jar
├─ README.txt
└─ user.pdf



D. Fonctionnalité: 
------------------

 Gestion des documents (Livres et Magazines)
   - Ajout, modification, suppression
   - Recherche par titre, auteur, genre

 Gestion des adhérents
   - Ajout, modification, suppression
   - Suivi du statut (pénalisé / en règle)
   - Consultation de l'historique d'emprunts

 Gestion des emprunts et retours
   - Vérification de la disponibilité
   - Limite de 5 emprunts simultanés par adhérent
   - Date de retour prévue : 3 semaines après l'emprunt
   - Calcul des pénalités de retard (0,50 € / jour)

 Persistance des données
   - Base SQLite (3 tables principales)
   - Chargement des documents, adhérents et emprunts au démarrage

 Interface graphique JavaFX
   - Design avec feuille de style CSS
   - Accès à toutes les fonctionnalités via des fenêtres dédiées
