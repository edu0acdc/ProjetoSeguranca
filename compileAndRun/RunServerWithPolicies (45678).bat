@ECHO OFF
cd SeiTchiz/serverCode
java "-Djava.security.manager" "-Djava.security.policy=serverCompiler.policy" -jar ..\server.jar SeiTchizServer 45678 keystore.server 123456
cd ..
PAUSE