@ECHO OFF
cd SeiTchiz/serverCode
del /s /q projeto1
rmdir projeto1 /s /q
cd ..
del server.jar
cd ..
cd SeiTchiz/clientCode
del /s /q projeto1
rmdir projeto1 /s /q
cd ..
del client.jar
cd ..
cd SeiTchiz/serverCode
cd ..
del /s /q server
rmdir server /s /q
cd ..
cd SeiTchiz/clientCode
cd ..
del /s /q client
rmdir client /s /q
cd ..
PAUSE
./compileALL.bat
PAUSE
