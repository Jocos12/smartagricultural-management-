# API Endpoints Documentation - Policy Dashboard

## Endpoints Utilisés par PolicyDataDashboard.html

### 1. Policy Data Endpoints
- **GET** `/api/policies` - Liste toutes les politiques (avec pagination)
- **GET** `/api/policies/{id}` - Détails d'une politique
- **POST** `/api/policies` - Créer une nouvelle politique
- **PUT** `/api/policies/{id}` - Mettre à jour une politique
- **DELETE** `/api/policies/{id}` - Supprimer une politique
- **GET** `/api/policies/statistics` - Statistiques des politiques
- **GET** `/api/policies/status/{status}` - Politiques par statut
- **GET** `/api/policies/type/{type}` - Politiques par type
- **GET** `/api/policies/category/{category}` - Politiques par catégorie
- **GET** `/api/policies/search` - Recherche de politiques
- **GET** `/api/policies/requiring-review` - Politiques nécessitant révision
- **GET** `/api/policies/expired` - Politiques expirées
- **GET** `/api/policies/low-performing` - Politiques peu performantes
- **PUT** `/api/policies/{id}/activate` - Activer une politique
- **PUT** `/api/policies/{id}/suspend` - Suspendre une politique
- **PUT** `/api/policies/{id}/renew` - Renouveler une politique

### 2. User Endpoints
- **GET** `/api/users/farmers` - Liste tous les users avec rôle FARMER
- **GET** `/api/users/{id}` - Détails d'un user
- **GET** `/api/users?role={role}` - Users par rôle

### 3. Farmer Endpoints
- **GET** `/api/farmers/user/{userId}` - Récupérer le Farmer par userId
  - **Note importante**: Cet endpoint peut retourner 404 si le User n'a pas encore de Farmer enregistré. C'est normal et géré gracieusement dans le frontend.
- **GET** `/api/farmers/{id}` - Détails d'un farmer par ID
- **GET** `/api/farmers` - Liste tous les farmers (avec pagination)

### 4. Farm Endpoints
- **GET** `/api/farms/farmer/{farmerId}/all` - Toutes les farms d'un farmer
- **GET** `/api/farms/{id}` - Détails d'une farm

## Gestion des Erreurs 404

### Cas Normal: User sans Farmer enregistré
Un User avec le rôle `FARMER` peut ne pas avoir encore de Farmer enregistré dans la table `farmers`. Dans ce cas:
- L'endpoint `/api/farmers/user/{userId}` retourne 404
- **C'est normal** et ne doit pas être considéré comme une erreur
- Le frontend gère cela gracieusement en utilisant les données du User comme fallback

### Solution Implémentée
Le frontend gère maintenant les 404 de cette manière:
```javascript
if (farmerResponse.status === 404) {
    // C'est normal - le User n'a pas encore de Farmer enregistré
    console.debug(`  → No farmer record found for user ${userId} (this is normal)`);
}
```

## Endpoints Recommandés (Optionnels)

Si vous voulez éviter les 404, vous pouvez créer ces endpoints optionnels:

### 1. Endpoint pour vérifier l'existence d'un Farmer
- **GET** `/api/farmers/exists/user/{userId}` - Vérifie si un Farmer existe pour un userId
  - Retourne: `{ "exists": true/false, "userId": "..." }`
  - **Note**: Cet endpoint existe déjà dans FarmerController!

### 2. Endpoint pour créer un Farmer depuis un User
- **POST** `/api/farmers/from-user/{userId}` - Crée un Farmer pour un User existant
  - Prend les données du User et crée un Farmer avec des valeurs par défaut

## Améliorations du Frontend

### Table Policies - Colonnes Bien Définies
La table Policies a maintenant des colonnes avec:
- **Policy Code** (150px, monospace, aligné à gauche)
- **Policy Name** (250px, aligné à gauche, avec ellipsis)
- **Type** (150px, aligné à gauche)
- **Category** (150px, aligné à gauche)
- **Status** (120px, centré, avec badge coloré)
- **Budget** (150px, aligné à droite, formaté en RWF)
- **Beneficiaries** (120px, aligné à droite, formaté)
- **Effective Date** (130px, centré)
- **Actions** (150px, centré)

### Gestion des Erreurs
- Les erreurs 404 pour `/api/farmers/user/{userId}` sont maintenant gérées silencieusement
- Les logs de debug sont utilisés au lieu de console.error pour les cas normaux
- Le frontend continue de fonctionner même si certains farmers n'ont pas de données Farmer

## Notes Importantes

1. **404 sur `/api/farmers/user/{userId}` est normal** - Un User FARMER peut ne pas avoir de Farmer enregistré
2. **Le frontend utilise les données du User comme fallback** - Les informations de base sont toujours disponibles
3. **Les locations sont construites intelligemment** - Utilise Farmer.location, puis province/district/sector, puis coordonnées GPS
4. **Toutes les tables ont maintenant des colonnes bien définies** - Chaque donnée est dans sa colonne avec le bon alignement
