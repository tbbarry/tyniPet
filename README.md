# TyniPet
#Etudiants:


Ange-Pavel ISHMWE  
Thierno Boubacar BARRY   
Thierno Amirou DIALLO   


#Ce qui marche:


Authentification,  
Creation d'une pétition  
Visualisation d'une pétition  
Affichage des pétitions (avec pagination)  
Affichage des pétitions les plus signées (top 100)  
Une pétition ne peut que contenir 40. 000 signataires  

#Ce qui ne marche pas:


Signature d'une pétition (sa marche en local) et toutes les méthodes sont prêtes   
1milions de signataires    
Protegé les rêquetes côté backend  
Affichage des pétitions d'un utilisateur (Toutes les méthodes sont dejà prêtes)  




Instalation:

Création du projet
gcloud app create
gcloud init
git clone https://github.com/momo54/webandcloud.git](https://github.com/tbbarry/tyniPet.git


Lancer en local  
    cd webandcloud
    mvn package
    mvn appengine:run

Deployer   
mvn appengine:deploy
gcloud app browse
