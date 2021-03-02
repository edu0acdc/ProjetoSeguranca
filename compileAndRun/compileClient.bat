@ECHO OFF
cls
ECHO Compiling class files
javac -d client/ ../src/projeto1/*.java ../src/projeto1/client/*.java ../src/projeto1/client/core/*.java
ECHO Class files created
cd client
ECHO Creating jar file
jar cfeP client.jar projeto1.client.RunClient -C . projeto1/
ECHO Jar file created, at compileAndRun/client/client.jar
cd ..
PAUSE
