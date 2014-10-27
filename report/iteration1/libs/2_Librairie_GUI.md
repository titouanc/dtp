Les requirements de l'interface graphique sont les suivants

* Afficher une fenêtre esthétique 
* Mettre des menus / boutons
* Incorporer une vue 2D/3D
* Compatible avec la lib 3D choisie


Les différentes possibilités de GUI sont :

* AWT (Abstract Window Toolkit)
* Swing
* SWT (Standard Widget Toolkit)
* JavaFX
* Apache Pivot
* Qt Jambi

### Swing
Nous avons retenu **Swing**.

* Customisable : Oui
* Léger : Non
* Cross-Platform : Oui
* Documentation : Complet
* Faiblement couplé
* Suit le design pattern MVC
* Compatible avec jMonkeyEngine

Toutes ces raisons font qu'on utilisera Swing comme librairie pour 
construire notre interface graphique.
Voici un aperçu des autres libraires éligibles, et les raisons pour 
lesquelles elles ont été refusées.

### AWT

* Customisable : Non
* Léger : Oui
* Cross-Platform : Oui
* Documentation : Complet
* AWT utilise les objets du système
* Swing hérite des objets de AWT

On n'utilisera pas AWT car il n'est pas assez customisable.

### SWT

* Customisable : Non
* Léger : Oui
* Cross-Platform : Oui
* Documentation : Très complet
* Développé par l'équipe d'Eclipse
* Se place un peu comme premier concurrent de Swing

On n'utilisera pas SWT car il n'est pas assez customisable.

### Java FX

On n'utilisera pas Java FX car ils se sont spécialisés dans les 
interfaces d'application web et ce n'est pas forcément utile ici.

### Apache Pivot

On n'utilisera pas Apache Pivot pour les mêmes raisons que Java FX.

### Qt Jambi

* Customisable : Oui
* Léger : Non
* Cross-Platform : Oui mais limité à QT4.6
* Documentation : Mauvaise
* Intégré à Eclipse
* Assez complet
* Accès a une database sql incluse

Inconvénients :

- Pas de doc sur le site QtJambi
- Compliqué pour l'intégration de la 3D . Il existe Qt3D mais on s'est 
dirigé vers Jmonkey
- Bloqué a la version 4.6
