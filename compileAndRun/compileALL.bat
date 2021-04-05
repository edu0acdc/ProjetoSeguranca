@ECHO OFF
cls
ECHO Compiling Client class files
javac -d SeiTchiz/clientCode/ ../src/projeto1/sharedCore/*.java ../src/projeto1/client/*.java ../src/projeto1/client/core/*.java  ../src/projeto1/client/exceptions/*.java
ECHO Client class files created
cd SeiTchiz
ECHO Creating client jar file
jar cfeP client.jar projeto1.client.RunClient -C . clientCode/projeto1/
cd ..

ECHO Compiling Server class files
javac -d SeiTchiz/serverCode/ ../src/projeto1/sharedCore/*.java ../src/projeto1/server/*.java ../src/projeto1/server/core/*.java ../src/projeto1/server/database/*.java  ../src/projeto1/server/handlers/*.java ../src/projeto1/server/threads/*.java ../src/projeto1/server/exceptions/*.java
ECHO Sever class files created
cd SeiTchiz
ECHO Creating server jar file
jar cfeP server.jar projeto1.server.RunServer -C . serverCode/projeto1/
cd ..
PAUSE