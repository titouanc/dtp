Utilisation de SQLite pour enregistrer les projets, à l'aide de 
ORMlite, et ses dépendances (ormlite-jdbc, ormlite-core, sqlite-jdbc)

* sqlite a les avantages d'une DB relationnelle, mais enregistre dans 
des fichiers: facilité de déplacement des projets, pas besoin de 
serveur
* sqlite permet d'avoir une DB en mémoire vive: pratique pour les tests
* sqlite est utilisable dans de nombreux autres langages: augmente la 
productivité en permettant d'éditer les fichiers de projets dans le 
langage préféré de chaque dev
* ORM facilite l'accès aux données (Design pattern DataMapper 
http://martinfowler.com/eaaCatalog/dataMapper.html)
