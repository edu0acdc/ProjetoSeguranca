@ECHO OFF
cls
ECHO Compiling class files
javac -d server/ ../src/projeto1/*.java ../src/projeto1/server/*.java ../src/projeto1/server/core/*.java ../src/projeto1/server/database/*.java  ../src/projeto1/server/handlers/*.java ../src/projeto1/server/threads/*.java
ECHO Class files created
cd server
ECHO Creating jar file
jar cfeP server.jar projeto1.server.RunServer -C . projeto1/
ECHO Jar file created, at compileAndRun/server/server.jar
cd ..
PAUSE
