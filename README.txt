DonutPay Notif - mod Fabric (client uniquement), Minecraft 1.21.11

COMPILER
1. Installe Java 21 et Gradle (ou ouvre le dossier dans IntelliJ IDEA).
2. Dans le dossier, lance :  gradle build
3. Le mod est dans build/libs/donutpay-1.0.0.jar
4. Mets-le dans .minecraft/mods avec Fabric Loader et Fabric API.

REGLAGES (dans DonutPayClient.java)
- DURATION_MS : durée de la notif (5000 = 5 secondes)
- PAY : regex qui lit le message de paiement. Si rien ne s'affiche,
  copie le message exact du chat quand on te paye et adapte-la.

OBTENIR LE .JAR SANS RIEN INSTALLER
1. Cree un depot GitHub (gratuit) et envoie-y tout le contenu de ce dossier
   (y compris le dossier .github).
2. Onglet Actions > build > Artifacts > donutpay-jar : tu telecharges le .jar.
