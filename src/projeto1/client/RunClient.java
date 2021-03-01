package projeto1.client;

import projeto1.client.core.Client;

public class RunClient{

	
	
	public static void main(String[] args) {
		if(args.length < 3 || args.length > 4) {
			System.out.println("Error: Usage -> SeiTchiz <serverAddress> <clientID> [password]");
		}
		
		
		String[] addr = args[1].split(":");
		
		String ip = addr[0];
		int port = 45678;
		if(addr.length == 2) 
			port = Integer.parseInt(addr[1]);
		
		
		Client client;
		if(args.length == 4) {
			client = new Client(ip, port,args[2],args[3]);
		}
		else {
			client = new Client(ip, port,args[2]);
		}		
		
		client.start();
	}
	
}
