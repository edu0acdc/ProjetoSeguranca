@ECHO OFF
cd server
del /s /q projeto1
rmdir projeto1 /s /q
del /s /q server
rmdir server /s /q
del server.jar
cd ..
cd client
del /s /q projeto1
rmdir projeto1 /s /q
del /s /q client
rmdir client /s /q
del client.jar
cd ..
PAUSE
./compileALL.bat
