# Your Car Your Way - Proof of Concept (PoC)

Ce dépôt contient le Proof of Concept (PoC) de la nouvelle architecture centralisée du système d'information de **Your Car Your Way**. 

Ce PoC se concentre sur la démonstration de l'architecture orientée **API-First** au travers d'une fonctionnalité isolée : **le tchat de support client en temps réel**.

Il comprend :
- Une base de données **PostgreSQL** conteneurisée.
- Un Back-end **Spring Boot** exposant une API REST et un flux SSE.
- Un Front-end **Angular**.

## Prérequis

Pour exécuter ce projet localement, vous devez avoir installé sur votre machine :
* [Docker](https://docs.docker.com/get-docker/) et [Docker Compose](https://docs.docker.com/compose/install/)
* [Java 21](https://adoptium.net/) et [Maven](https://maven.apache.org/) (pour le Back-end)
* [Node.js](https://nodejs.org/) (version 18+) et [Angular CLI 21](https://angular.io/cli) (pour le Front-end)


## Configuration des variables d'environnement

Pour des raisons de sécurité, les identifiants de la base de données ne sont pas versionnés en clair. Avant de lancer les différents services, vous devez configurer vos variables d'environnement.

1. Dans le dossier `back/`, créez un fichier nommé `.env`.
2. Ajoutez-y les lignes suivantes :
``` bash
DB_USER=votre_nom_d_utilisateur
DB_PASSWORD=votre_mot_de_passe
```

## 1. Démarrage de la Base de données (Docker)

L'environnement de base de données est totalement isolé, garantissant un fonctionnement identique sur toute machine.

1. Ouvrez un terminal dans le dossier `back/` (où se trouve le `docker-compose.yml`).
2. Exécutez la commande suivante :
   ```bash
   docker-compose up -d
   ```

## 2. Démarrage du Back-end (Spring Boot)

Ouvrez un terminal dans le dossier `back/` et lancez l'application via Maven :
```bash
mvn spring-boot:run
```
L'API sera disponible sur `http://localhost:8080`.

## 3. Démarrage du Front-end (Angular)

1. Ouvrez un terminal dans le dossier `front/`.
2. Installez les dépendances du projet :
```bash
npm install
```
3. Démarrez le serveur de développement :
```bash
ng serve
```
L'interface utilisateur sera disponible sur `http://localhost:4200`.

## Bonnes pratiques

- Sécurité : Les mots de passe et configurations sensibles ne sont pas écrites en clair dans le code (utilisation de variables d'environnement).
- Interopérabilité : L'utilisation de types UUID pour les clés primaires facilite la fusion des bases de données internationales existantes.
- Architecture Réactive : L'utilisation du Server-Sent Events (SSE) plutôt que des WebSockets permet un flux unidirectionnel en temps réel plus léger et utilisant les standards HTTP classiques, idéal pour l'envoi de messages de support client.