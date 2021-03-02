@ECHO OFF
cls
ECHO Compiling Client class files
javac -d client/ ../src/projeto1/*.java ../src/projeto1/client/*.java ../src/projeto1/client/core/*.java
ECHO Client class files created
cd client
ECHO Creating client jar file
jar cfeP client.jar projeto1.client.RunClient -C . projeto1/
ECHO Client jar file created, at compileAndRun/client/client.jar
cd ..

ECHO Compiling Server class files
javac -d server/ ../src/projeto1/*.java ../src/projeto1/server/*.java ../src/projeto1/server/core/*.java ../src/projeto1/server/database/*.java  ../src/projeto1/server/handlers/*.java ../src/projeto1/server/threads/*.java
ECHO Sever class files created
cd server
ECHO Creating server jar file
jar cfeP server.jar projeto1.server.RunServer -C . projeto1/
ECHO Server jar file created, at compileAndRun/server/server.jar
cd ..
PAUSE