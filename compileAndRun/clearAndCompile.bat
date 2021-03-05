@ECHO OFF
cd server
rm -R projeto1
rm -R server
rm server.jar
cd ..
cd client
rm -R projeto1
rm -R client
rm client.jar
cd ..
./compileALL.bat
