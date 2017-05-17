Dataverse Bridge to push datasets from Dataverse temporary repository to TDR

Available options are:
- DANS EASY 
- Islandora
- Archivematica

# Installation
git clone https://github.com/DANS-KNAW/easy-sword2-dans-examples
cd easy-sword2-dans-examples
mvn clean install

git clone https://github.com/DANS-KNAW/dataverse-bridge
mvn clean install

# Deploy application in Glassfish 
/usr/local/glassfish4/bin/asadmin deploy dataverse-bridge-1.0-SNAPSHOT.war
