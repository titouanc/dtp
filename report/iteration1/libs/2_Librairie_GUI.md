### Requirements de l'interface graphique


* Afficher une fenêtre esthétique 
* Mettre des menus / boutons
* Incorporer une vue 2D/3D
* Compatible avec la lib 3D choisie


### Choix possibles 

Les différentes possibilités de GUI sont :

* AWT (Abstract Window Toolkit)
* Swing
* SWT (Standard Widget Toolkit)
* JavaFX
* Apache Pivot
* Qt Jambi

### Choix retenu

#### Swing

Customisable : Oui
Léger : Non
Cross-Platform : Oui
Documentation : Complet

Informations :

* Faiblement couplé
* Suit le design pattern MVC
* Compatible avec jmonkey engine

Toutes ces raison font qu'on utilisera Swing comme librairie pour 
construire notre interface graphique.

### Choix refusé

#### AWT

Customisable : Non
Léger : Oui
Cross-Platform : Oui
Documentation : Complet

Informations :

* AWT utilise les objets du système
* Swing hérite des objets de AWT

On n'utilisera pas AWT car il n'est pas assez customisable.

#### SWT

Customisable : Non
Léger : Oui
Cross-Platform : Oui
Documentation : Très complet

Informations :

- Développé par l'équipe d'Eclipse
- Se place un peu comme premier concurrent de Swing

On n'utilisera pas AWT car il n'est pas assez customisable.

#### Java FX

On utilisera pas Java FX car ils se sont spécialiser dans les 
interfaces d'application web et ce n'est pas forcément utile ici.

#### Apache Pivot

On utilisera pas Apache Pivot pour les mêmes raisons que Java FX.

#### Qt Jambi

Customisable : Oui
Léger : Non
Cross-Platform : Oui mais limité à QT4.6
Documentation : Mauvaise

Avantage : 

- Intégré a éclipse
- Assez complet
- acces a une database sql incluse

Inconvenient :

- Pas de doc sur le site QtJambi
- Compliqué pour l'intégration de la 3D . Il existe Qt3D mais on s'est 
dirigé vers Jmonkey
- Bloqué a la version 4.6
