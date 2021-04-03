package projeto1.client;

import projeto1.client.core.Client;

public class RunClient{

	private static final String USAGE = "Error: Usage -> SeiTchiz <serverAddress> <truststore> <keystore> <keystore-password> <clientID>";
	
	public static void main(String[] args) {
		if(args.length != 6) {
			System.out.println(USAGE);
			return;
		}
		
		if(!args[0].contentEquals("SeiTchiz")) {
			System.out.println(USAGE);
			return;
		}
		
		
		String[] addr = args[1].split(":");
		
		String ip = addr[0];
		int port = 45678;
		if(addr.length == 2) 
			port = Integer.parseInt(addr[1]);
		
		String truststore = args[2];
		String keystore = args[3];
		String keystore_password = args[4];
		String clientID = args[5];
		
		Client client = new Client(ip, port, clientID,truststore,keystore,keystore_password);
		client.start();
	}
	
}
