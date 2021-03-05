@ECHO OFF
cd server
java "-Djava.security.manager" "-Djava.security.policy=serverCompiler.policy" -jar .\server.jar SeiTchizServer 45678
cd ..
PAUSE