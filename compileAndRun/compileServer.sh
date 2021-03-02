@ECHO OFF
clear
echo Compiling class files
javac -d server/ ../src/projeto1/*.java ../src/projeto1/server/*.java ../src/projeto1/server/core/*.java ../src/projeto1/server/database/*.java  ../src/projeto1/server/handlers/*.java ../src/projeto1/server/threads/*.java
echo Class files created
cd server
echo Creating jar file
jar cfeP server.jar projeto1.server.RunServer -C . projeto1/
echo Jar file created, at compileAndRun/server/server.jar
cd ..
read -s -n 1 -p "Press any key to continue . . ."
echo ""
