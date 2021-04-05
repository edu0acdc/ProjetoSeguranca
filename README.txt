||||||||||||||||||||||||||||||||||||||||||||||||
||   PROJETO DE SEGURANÇA E CONFIABILIDADE    ||
|| FASE 2 -------------------- GRUPO SegC-070 ||
||||||||||||||||||||||||||||||||||||||||||||||||

O trabalho foi desenvolvido e testado em ambiente Eclipse EE e também em Windows Shell.
------------------------------------------------------------------------------------------------

Estrutura

|--> Projeto
      |--> JARS (Jars gerados pelo o Eclipse sem sandbox)
      |--> bin (.class gerados pelo Eclipse)
      |--> client (pasta afeta ao cliente gerada pelos testes em Eclipse)
      |--> server (pasta afeta ao servidor gerada pelos testes em Eclipse) 
      |--> src (Código-Fonte)
      |--> compileAndRun (pasta com scripts para gerar .class e .jar apartir do código-fonte)
 
------------------------------------------------------------------------------------------------

Policies

-> As policies em ./Projeto foram criadas para testagem em Eclipse
-> As policies em ./Projeto/JARS/(server/client) foram criadas para a execução dos jars nas pastas respectivas, contudo se foram copiadas para ./JARS irão reconhecer os JARS (server/client).jar

------------------------------------------------------------------------------------------------

Compilação e Execução

-> Todas as keystores têm a password 123456 e todas as keys também
-> Recomenda-se a utilização da flag --auto no cliente (SeiTchiz 127.0.0.1:45678 truststore.client <Nome> --auto) , será gerado tudo o que é necessario para correr o cliente de forma automatica.
-> Se for criado manualmente as passwords devem ser todas 123456 , e a key gerada na keystore.client deve ter o alias "client"
-> As certificados são escritos e lidos com o nome "<Nome>.cer" na pasta PubKeys
-> Mais uma vez para efeitos de testagem recomenda-se a utilização da flag --auto
-> Nesta fase do trabalho já é possivel ter varios clientes na mesma maquina de forma indenpendente, tendo assim cada utilizador a sua pasta pessoal dentro da pasta client.


EXEMPLOS:
java "-Djava.security.manager" "-Djava.security.policy=server.policy" -jar .\server.jar SeiTchizServer 45678 keystore.server 123456
java "-Djava.security.manager" "-Djava.security.policy=client.policy" -jar .\client.jar SeiTchiz 127.0.0.1:45678 truststore.client Edu --auto
java "-Djava.security.manager" "-Djava.security.policy=client.policy" -jar .\client.jar SeiTchiz 127.0.0.1:45678 truststore.client keystore.client 123456 Edu

A keystore será procurada dentro da pasta client/user/keystore.client
A truststore.client será procurada na pasta de execução

------------------------------------------------------------------------------------------------
