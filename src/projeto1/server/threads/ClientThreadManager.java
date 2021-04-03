package projeto1.server.threads;

import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class ClientThreadManager{

	
	private List<ClientThread> clients;
	private static  ClientThreadManager singleton = null;
	private SecureRandom sr;
	
	private ClientThreadManager() {
		clients = new ArrayList<>();
		
		sr = new SecureRandom();
	}
	
	public static ClientThreadManager getManager() {
		if(singleton == null) {
			singleton = new ClientThreadManager();
		}
		return singleton;
	}
	
	public boolean addNewClient(Socket socket) {
		byte[] b = new byte[8];
		sr.nextBytes(b);
		ClientThread c = new ClientThread(socket,this,b);
		c.start();
		clients.add(c);
		return true;
	}


	public void deleteFromManager(ClientThread ct) {
		clients.remove(ct);
	}
	
	
	
}
