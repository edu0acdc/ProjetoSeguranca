@ECHO OFF
clear
echo Compiling class files
javac -d client/ ../src/projeto1/*.java ../src/projeto1/client/*.java ../src/projeto1/client/core/*.java
echo Class files created
cd client
echo Creating jar file
jar cfeP client.jar projeto1.client.RunClient -C . projeto1/
echo Jar file created, at compileAndRun/client/client.jar
cd ..
read -s -n 1 -p "Press any key to continue . . ."
echo ""
