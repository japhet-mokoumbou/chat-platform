Plateforme de Discussion (Chat Platform)
Plateforme de discussion en ligne de type WhatsApp. L'application permet l'inscription, la connexion, la gestion des contacts, des groupes, des messages/fichiers, et des paramètres, avec un stockage des données au format XML.
Structure du projet

backend/ : API RESTful avec Spring Boot, Spring Security, JPA, et JAXB pour le stockage XML.
frontend/ : Application web React avec Tailwind CSS pour l'interface utilisateur.

Prérequis

Java 17 : Pour le backend.
Node.js 18+ : Pour le frontend.
Maven : Pour gérer les dépendances du backend.
Git : Pour cloner le dépôt.

Installation

Cloner le dépôt :
git clone https://github.com/japhet-mokoumbou/chat-platform.git
cd chat-platform


Configurer le backend :
cd backend
mvn install


Configurer le frontend :
cd frontend
npm install



Exécution

Lancer le backend :
cd backend
mvn spring-boot:run

Le backend sera disponible sur http://localhost:8080.

Lancer le frontend :
cd frontend
npm start

Le frontend sera disponible sur http://localhost:3000.

Tester la connexion :

Ouvre http://localhost:3000 dans un navigateur.
Vérifie que la page d'accueil affiche "Backend is running" (appel à GET /api/test).



Prochaines étapes

Implémenter l'inscription et la connexion (endpoints /register, /login).
Ajouter la gestion des contacts, groupes, messages, et profils.
Configurer le stockage XML pour toutes les données.

Livraison

Soumettre via Classroom avant le 15 juillet 2025.
Préparer une présentation pour le 16 juillet 2025.
