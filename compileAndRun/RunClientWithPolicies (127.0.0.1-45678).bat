@ECHO OFF
cd SeiTchiz
java "-Djava.security.manager" "-Djava.security.policy=clientCompiler.policy" -jar .\client.jar SeiTchiz 127.0.0.1:45678 admin
cd ..
PAUSE