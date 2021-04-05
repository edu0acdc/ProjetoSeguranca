package projeto1.client;

import java.security.PrivateKey;

import projeto1.client.core.Client;
import projeto1.client.core.ClientLoader;
import projeto1.client.exceptions.ClientLoadingException;

public class RunClient{

	private static final String USAGE = "Usage -> SeiTchiz <serverAddress> <truststore> <keystore> <keystore-password> <clientID>\nOR\n"
			+ "Usage -> SeiTchiz <serverAddress> <truststore> <clientID> --auto";
	
	public static void main(String[] args) {
		if(args.length == 6 || args.length == 5) {
			if(!args[0].contentEquals("SeiTchiz")){
				System.out.println(USAGE);
				return;
			}
			try {
				String[] addr = args[1].split(":");
				String ip = addr[0];
				int port = 45678;
				if(addr.length == 2) 
					port = Integer.parseInt(addr[1]);
				String truststore = args[2];
				if(args.length == 5) {
					if(!args[4].contentEquals("--auto")) {
						System.out.println(USAGE);
						return;
					}
					String clientID = args[3];
					PrivateKey pk = ClientLoader.autoLoad(clientID, truststore);
					Client client = new Client(ip, port, clientID,pk);
					client.start();
				}
				else {
					String keystore = args[3];
					String keystore_password = args[4];
					String clientID = args[5];
					PrivateKey pk = ClientLoader.load(clientID, truststore, keystore, keystore_password);
					Client client = new Client(ip, port, clientID,pk);
					client.start();
				}
			}catch (ClientLoadingException e) {
				e.printStackTrace();
				System.out.println("ERROR: NOT POSSIBLE TO START CLIENT");
			}
		}
		else {
			System.out.println(USAGE);
		}		
	}
	
}
