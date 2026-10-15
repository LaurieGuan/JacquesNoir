# Conception — JacquesNoir

## Objectif et portée

Application Android locale de blackjack à un joueur contre un croupier automatisé. Le MVP permet de démarrer/reprendre une partie, jouer une manche, consulter l’historique et les statistiques. Aucun argent réel, multijoueur, assurance ou service distant. L’interface artistique, les commandes par mouvement, les parties en pause et l’œuf de Pâques sont des extensions après le MVP.

## Architecture

Une seule `Activity` Compose héberge une navigation entre Accueil, Jeu, Mise, Statistiques et Historique. Chaque écran utilise `Scaffold` (barre supérieure, navigation inférieure et FAB adaptés à l’action disponible). Les actions impossibles sont masquées ou désactivées selon l’état courant.

```text
Compose (Navigation / écrans)
        ↕ StateFlow / actions
ViewModels (viewmodelScope, collectAsStateWithLifecycle)
        ↕
FacadeModele (règles et état de partie)
   ├── DepotPartie ── DAO ── Room / SQLite
   └── DepotStatistiques ── requêtes DAO ── Room / SQLite
```

La façade du modèle applique les règles et coordonne les dépôts; elle n’est pas un moteur distinct. Room est l’unique source de vérité persistante pour les parties et l’historique. Les ViewModels exposent des `StateFlow`; au moins une lecture observable de Room passe par `Flow`, transformé au besoin avec `stateIn`. Les écritures utilisent des fonctions `suspend` lancées depuis `viewModelScope`. La base est créée une fois au niveau de l’application et injectée dans les dépôts.

## Modèle de domaine et persistance

- **Partie** : identifiant, solde de jetons, mise par défaut, état (en cours/terminée), dates. Une partie contient un joueur et le croupier.
- **Manche** : partie, mise, état, résultat et horodatage. Une manche contient une ou plusieurs mains.
- **Main** : acteur, index, mise courante, état (active, arrêtée, sautée), cartes dans leur ordre de distribution.
- **Carte** : rang (as à roi) et couleur; les 52 cartes forment un sabot mélangé. La défausse conserve les cartes retirées jusqu’au renouvellement du sabot.

Room devrait représenter les entités Partie, Manche, Main et Carte/jouée (ou une table de cartes associée avec index d’ordre). Le canvas propose aussi Joueur, Sabot, Défausse, Settings et statistiques d’apparition. Pour le MVP à joueur unique, l’acteur peut être un rôle joueur/croupier plutôt qu’une table Joueur généralisée. Les statistiques affichent, pour chaque carte, son nombre d’apparitions et son pourcentage d’apparition. Elles sont obtenues par des requêtes Room/SQLite sur les cartes distribuées.

## Déroulement d’une manche

1. Le joueur choisit une mise couverte par son solde; celle-ci est réservée.
2. Distribution alternée de deux cartes au joueur et au croupier, la seconde carte du croupier face cachée.
3. Le joueur tire, reste, double, sépare ou abandonne selon les règles et l’état de sa main. Après séparation, chaque main joue successivement.
4. Le croupier révèle sa carte et tire automatiquement selon la règle fixée ci-dessous.
5. Chaque main est réglée séparément; le solde et le résultat sont enregistrés, puis une nouvelle manche peut commencer.

## Hypothèses à valider / décisions explicites pour le MVP

- **As** : vaut 11 ou 1 automatiquement pour maximiser le total sans dépasser 21.
- **Croupier** : tire jusqu’à 17 et reste sur soft 17 (as compté 11).
- **Blackjack** : deux cartes initiales totalisant 21; paiement net de 3:2, sauf égalité contre blackjack du croupier, qui est une égalité.
- **Paiements** : victoire ordinaire = gain net égal à la mise; égalité = mise rendue; défaite/saut = mise perdue; blackjack = gain net de 1,5× la mise. Un saut du croupier gagne les mains encore en jeu selon le paiement ordinaire.
- **Doubler** : ajouter une mise égale à la mise de la main, puis recevoir une seule carte et terminer cette main. Autorisé sur les mains issues d’une séparation.
- **Séparer** : deux cartes de même rang; chaque main reçoit une carte. Mise égale ajoutée à la seconde main. Une seule séparation est autorisée par main; aucune re-séparation.
- **Abandon** : hors MVP.
- **Solde insuffisant** : mise, doublement et séparation ne sont disponibles que si le joueur peut couvrir le montant supplémentaire. Le solde à zéro termine la partie. L’œuf de Pâques n’est pas implémenté dans le MVP.
- **Sabot** : le joueur choisit de 1 à 4 paquets au début de la partie. Le sabot est jeté et remplacé par un nouveau sabot mélangé lorsque le nombre de cartes restantes ne suffit pas pour une manche complète.
- **Persistance/reprise** : enregistrer après chaque action qui modifie l’état; reprendre la dernière partie en cours après fermeture. L’écran de liste des parties en pause reste hors MVP.

## Interface et adaptation

Les ressources graphiques représentent les cartes, y compris le dos de la carte cachée. L’écran de jeu rend visibles la mise, le solde, les totaux et l’action courante. Portrait et paysage sont pris en charge sans changer la logique du domaine; les commandes par mouvement ne remplacent pas les boutons accessibles au MVP. Les animations, le bras 8-bit, les sons et le style pixel art/photo sont des améliorations visuelles indépendantes des règles.

## Points à confirmer avant implémentation

Les règles confirmées sont : soft 17, paiement blackjack 3:2, une séparation maximum par main, abandon hors MVP, choix de 1 à 4 paquets, statistiques de fréquence des cartes et remplacement du sabot lorsqu’il est insuffisant pour une manche complète.
