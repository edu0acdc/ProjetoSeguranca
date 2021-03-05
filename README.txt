||||||||||||||||||||||||||||||||||||||||||||||||
||   PROJETO DE SEGURANÇA E CONFIABILIDADE    ||
|| FASE 1 -------------------- GRUPO SegC-070 ||
||||||||||||||||||||||||||||||||||||||||||||||||

O trabalho foi desenvolvido e testado em ambiente Eclipse EE e também em Windows Shell.

------------------------------------------------------------------------------------------------

Estrutura

|
|--> JARS (Jars gerados pelo o Eclipse sem sandbox)
|--> Projeto
      |
      |--> bin (.class gerados pelo Eclipse)
      |--> client (pasta afeta ao cliente gerada pelos testes em Eclipse)
      |--> server (pasta afeta ao servidor gerada pelos testes em Eclipse) 
      |--> src (Código-Fonte)
      |--> compileAndRun (pasta com scripts para gerar .class e .jar apartir do código-fonte)
 
------------------------------------------------------------------------------------------------

Policies

-> As policies em ./Projeto foram criadas para testagem em Eclipse
-> As policies em ./Projeto/compileAndRun/(server/client) foram criadas para a execução dos jars nas pastas respectivas, contudo se foram copiadas para ./JARS irão reconhecer os JARS (server/client).jar

------------------------------------------------------------------------------------------------

Código

-> Sobre os métodos msg/collect/history foi primeiramente criado um algoritmo em que cada cliente tinha o seu historico que iria aumentar cada vez que o mesmo fizesse collect,
   depois de uma reflexão sobre o enunciado o algoritmo foi alterado para todos clientes de um grupo ter um historico comum que só quando cresce quando todos os membros lerem
   uma certa mensagem, i.e, todos os membros ainda pertecentes ao grupo à data do envio da mensagem fizerem collect da mesma. Por fim, quando uma pessoa envia uma mensagem é  assumido que o sender já viu a mensagem que enviou.

-> As fotos para o método post devem ser colocadas na pasta client/photos.

-> Visto que não é feita nenhuma referência a ser possivel dois clientes na mesma máquina, a pasta client é unica para qualquer execução do client.jar


------------------------------------------------------------------------------------------------

Compilação e Execução

-> Em ./Projeto/compileAndRun existe scripts para compilar,correr o cliente e correr o servidor. Todas possuem uma versão Windows(.bat) e Linux(.sh).
-> Todos os comandos necessários para efeitos de compilação e execução podem ser vistos com um editor de texto nas scripts referidas anteriormente.

------------------------------------------------------------------------------------------------
