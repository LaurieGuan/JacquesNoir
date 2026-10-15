# JacquesNoir — Dictionnaire visuel
## 1. Thème général de l’interface
L’interface de JacquesNoir s’inspire d’une bureaucratie ancienne, pré-numérique, avec une influence visuelle soviétique et rétrofuturiste. L’objectif n’est pas de représenter explicitement un gouvernement ou une époque historique précise, mais de faire transparaître cet univers par la mise en page, les couleurs, la typographie et la forme des composants.
L’application doit évoquer un système administratif fonctionnel mais austère : formulaires, rapports imprimés, lignes à remplir, références de dossier, caractères de machine à écrire et mise en page rigide.
Le blackjack représente cependant une rupture avec cette bureaucratie. Le jeu correspond davantage à un moment de détente après le travail : il conserve les matériaux, les couleurs et certains codes graphiques du même monde, mais abandonne une grande partie de la rigidité des documents administratifs.
La distinction générale est donc :
ADMINISTRATION
rigide · structurée · documentaire · procédurale

          ↓ même univers visuel ↓

BLACKJACK
tactile · direct · spatial · plus libre

L’histoire derrière cet univers ne doit jamais être expliquée explicitement dans l’interface. Elle doit uniquement être perceptible à travers les choix de design.

## 2. Langage visuel principal
### 2.1 Structure bureaucratique
Les écrans administratifs utilisent principalement :
- des surfaces ivoire rappelant le papier;
- des alignements francs;
- des bordures noires fines;
- des titres en capitales;
- des séparateurs horizontaux;
- des lignes soulignées rappelant les champs de formulaire;
- une typographie monospace ou inspirée de la machine à écrire;
- très peu de coins arrondis;
- peu ou pas d’ombres modernes.
Les interfaces doivent éviter de ressembler à des cartes Material Design superposées. La hiérarchie est créée principalement par la typographie, les lignes et les espaces.
### 2.2 Blackjack
L’écran de jeu conserve la palette et la typographie générale, mais devient beaucoup moins documentaire.
Les cartes, jetons et piles sont des objets physiques dans l’espace plutôt que des champs administratifs.
Les cartes constituent les éléments les plus expressifs de l’écran :
- face ivoire;
- contours noirs;
- structure graphique orange en mode clair;
- rouge pour les enseignes rouges et certains accents;
- superposition des cartes;
- très peu d’effets décoratifs.
### 2.3 Rétrofuturisme
Le futurisme de JacquesNoir repose principalement sur l’opposition entre les couleurs chaudes de la machine et les couleurs froides de l’espace.
ORANGE / ROUGE                CYAN
────────────────────────────────────────
machine                       espace
industrie                     inconnu
production                    exploration
État                          technologie
progrès                       environnement à conquérir
chaleur mécanique             froideur cosmique

## 3. Justification du langage de couleur
Neutres — bureaucratie et matière
L’ivoire, le kaki, le charbon et le noir constituent le backbone de l’interface.
Ils représentent :
- le papier;
- les classeurs;
- les bureaux;
- les machines;
- l’encre;
- les surfaces institutionnelles.
Ils doivent occuper la majorité de l’écran afin que les couleurs d’accent restent significatives.
Orange — machine et progrès
L’orange industriel représente la machine, l’activité et le progrès technique.
En mode clair, il constitue l’accent interactif principal.
Il apparaît notamment sur :
- les actions importantes;
- certaines structures des cartes;
- les marquages des jetons;
- les éléments indiquant une activité ou un processus.
Rouge — autorité et conséquence
Le rouge doit être utilisé beaucoup plus rarement.
Il représente :
- l’autorité;
- les avertissements;
- les conséquences;
- les pertes;
- certains tampons;
- les enseignes rouges des cartes.
Sa rareté lui donne davantage de poids.
Cyan — espace et inconnu
Le cyan représente :
- l’espace;
- l’exploration;
- la technologie;
- l’inconnu;
- la profondeur.
En mode clair, il doit rester relativement rare, puisqu’il représente quelque chose d’extérieur au quotidien administratif.
En mode sombre, son importance augmente fortement : l’utilisateur se trouve alors visuellement dans cet univers nocturne et technologique.
## 4. Différence entre mode clair et mode sombre
Le mode sombre ne doit pas être une simple inversion du mode clair.
Mode clair — Bureau
Le mode clair représente principalement le monde administratif.
Structure    → ivoire / kaki / noir
Action       → orange
Autorité     → rouge
Espace       → cyan

Mode sombre — Après les heures
Le mode sombre représente davantage la nuit, l’espace et la technologie.
Structure    → charbon / gris sombre
Action       → cyan
Futur        → orange
Autorité     → rouge
Inconnu      → cyan profond

Dans le dark mode, le cyan devient donc davantage le milieu dans lequel se trouve l’utilisateur, tandis que l’orange conserve son association avec le progrès et le futur.
## 5. Principes d’utilisation
1. Les couleurs neutres doivent toujours dominer l’écran.
2. L’orange, le rouge et le cyan ne doivent pas servir de décoration arbitraire.
3. Chaque couleur d’accent doit conserver une signification relativement stable.
4. Le rouge doit être plus rare que l’orange.
5. Le cyan doit être plus rare en mode clair que dans le mode sombre.
6. Les cartes doivent se distinguer du fond principalement par leur luminosité et leur silhouette.
7. Le dark mode doit conserver les mêmes principes graphiques plutôt que simplement inverser les couleurs.
Le principe général du système peut être résumé ainsi :
La bureaucratie est neutre, la machine est chaude, l’autorité est rouge et l’inconnu est cyan.

## 6. Dictionnaire des couleurs
Le dictionnaire suivant utilise des tokens sémantiques, plutôt que des couleurs brutes. Cela permet aux composants Jetpack Compose de conserver le même rôle visuel entre le mode clair et le mode sombre.
````csv
Nom_Couleur;couleur_lightmode;couleur_darkmode
BackgroundPrimary;#E3D5C1;#111312
BackgroundSecondary;#8C8670;#182120
SurfacePrimary;#E3D5C1;#242A27
SurfaceSecondary;#8C8670;#333934
TextPrimary;#000000;#E3D5C1
TextSecondary;#1A1A18;#A9AA9C
BorderPrimary;#000000;#7D8781
BorderMuted;#8C8670;#3F4A46
AccentAction;#DB5B00;#78C6C8
AccentInteractive;#DB5B00;#4AA8B0
AccentFuture;#DB5B00;#DB5B00
AccentAuthority;#8F0000;#8F0000
AccentSpace;#78C6C8;#78C6C8
AccentUnknown;#245D63;#245D63
CardFace;#E3D5C1;#D8CBB7
CardText;#1A1A18;#1A1A18
CardStructure;#DB5B00;#78C6C8
CardRedSuit;#8F0000;#8F0000
CardBlackSuit;#000000;#1A1A18
TokenLight;#E3D5C1;#D8CBB7
TokenMid;#8C8670;#333934
TokenDark;#1A1A18;#111312
TokenMarking;#DB5B00;#78C6C8
TokenHighValue;#8F0000;#8F0000
```
