cd server
java "-Djava.security.manager" "-Djava.security.policy=serverCompiler.policy" -jar .\server.jar SeiTchizServer 45678
cd ..
read -s -n 1 -p "Press any key to continue . . ."
echo ""