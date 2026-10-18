# Gestion gNet — API de gestion des agences

Projet de stage : backend **Spring Boot / Java 17** pour consulter et administrer un annuaire d'agences. API REST documentée par Swagger, authentification JWT, contrôle d'accès par rôle et stockage JPA.

> **Portée de cette archive :** seul le backend Java a été fourni. Le frontend Angular, s'il existe, doit être ajouté séparément. Le projet n'est pas présenté comme une application full-stack entièrement testée.

## Fonctionnalités

- Consulter la liste et les détails des agences ; rechercher une agence par région.
- Créer, modifier, supprimer une agence et activer/désactiver son état (administrateur).
- Connexion sécurisée et utilisation d'un jeton JWT (24 heures).
- Documentation OpenAPI/Swagger : `http://localhost:8081/swagger-ui/index.html`.

## Configuration

Pré-requis : **JDK 17+** et **Maven 3.9+** (ou les scripts `mvnw`/`mvnw.cmd`).

Le projet charge des variables d'environnement (il **ne lit pas automatiquement** le fichier `.env`). Pour les exécutions de démonstration sous PowerShell :

```powershell
$env:JWT_SECRET = 'REMPLACER_PAR_UN_SECRET_ALEATOIRE_DE_PLUS_DE_32_CARACTERES'
$env:APP_ADMIN_USERNAME = 'admin'
$env:APP_ADMIN_PASSWORD = 'REMPLACER_PAR_UN_MOT_DE_PASSE_LONG_ET_UNIQUE'
.\mvnw.cmd spring-boot:run '-Dspring-boot.run.profiles=demo'
```

La configuration `demo` utilise H2 en mémoire. Les données de démonstration ne sont pas conservées après l'arrêt du serveur. Les identifiants de l'administrateur initial ne sont créés que si ces variables sont définies et que le compte n'existe pas encore. En production, définissez `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` pour MySQL ; créez d'abord la base de données `globalnet` et configurez un secret JWT fort et privé. La propriété `JPA_DDL_AUTO=update` n'est adaptée qu'au développement ; utiliser des migrations de schéma en production.

## Tests

```powershell
.\mvnw.cmd test
```

Le profil de test emploie H2 et contient des tests d’intégration MockMvc : lecture publique, écriture protégée, authentification JWT, création par administrateur, recherche par région, validation des données, réponse 404 et blocage de l’ancienne route non sécurisée. Ces tests ont été ajoutés, mais leur exécution n’a pas encore pu être vérifiée dans cet environnement.

## Endpoints

| Méthode | URL | Accès |
|---|---|---|
| POST | `/auth/login` | Public |
| GET | `/api/agences` | Public |
| GET | `/api/agences/{id}` | Public |
| GET | `/api/agences/search?region=Tunis` | Public |
| POST | `/api/agences` | JWT `ADMIN` |
| PUT | `/api/agences/{id}` | JWT `ADMIN` |
| DELETE | `/api/agences/{id}` | JWT `ADMIN` |
| PUT | `/api/agences/{id}/toggleEtat` | JWT `ADMIN` |

**Exemple de connexion :**

```powershell
$body = @{ username='admin'; password=$env:APP_ADMIN_PASSWORD } | ConvertTo-Json
Invoke-RestMethod -Uri http://localhost:8081/auth/login -Method Post -ContentType 'application/json' -Body $body
```

**Exemple d'agence :**

```json
{"nom":"Agence Tunis","adresse":"Tunis Centre","telephone":"71123456","horraire":"08:00-17:00","etat":true}
```

## Recommandations de publication

Ce code dérive d'un projet de stage : sa publication a été autorisée par le propriétaire du dépôt, qui déclare disposer de l’autorisation de GlobalNet. Vérifier séparément l’absence de secrets et données client avant toute mise en ligne publique. Ne jamais transférer `.env`, des bases réelles, des données client, des jetons, des secrets ou les dossiers `.idea`, `.git` et `target`. La clé JWT et la route publique de réinitialisation présentes dans l'archive initiale ont été retirées. Si cette clé a été utilisée en dehors du poste local, la révoquer/renouveler.

## Statut

Sources nettoyées et tests HTTP ajoutés ; l'exécution complète et l'intégration Angular restent à vérifier dans un environnement avec Maven, dépendances Java et frontend.

## Scripts PowerShell fournis

- **`demarrer-demo.ps1`** : configure un secret JWT et un mot de passe admin temporaires aléatoires, puis lance le serveur avec H2. Aucun secret n'est écrit dans un fichier. Sous PowerShell : `powershell -ExecutionPolicy Bypass -File .\demarrer-demo.ps1`.
- **`publier-sur-github.ps1`** : initialise Git et crée le dépôt `baaahaaaa/globalnet-agency-management` via GitHub CLI en privé par défaut. Installer `gh`, puis exécuter `gh auth login` et `powershell -ExecutionPolicy Bypass -File .\publier-sur-github.ps1`. Pour un dépôt public, utiliser `-Visibility public` après vérification des sources et de l'autorisation de publication.

**Important** : le script de publication ne contourne pas les autorisations GitHub. Il ne peut pas créer le dépôt sans authentification `gh` disposant du droit de créer des dépôts. Il s'arrête s'il existe déjà un dépôt de ce nom.
