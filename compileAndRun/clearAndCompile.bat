@ECHO OFF
cd server
del /s /q projeto1
del /s /q server
del server.jar
cd ..
cd client
del /s /q projeto1
del /s /q client
del client.jar
cd ..
PAUSE
./compileALL.bat
