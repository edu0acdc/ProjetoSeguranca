package projeto1.server.threads;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientThreadManager{

	
	private List<ClientThread> clients;
	private static  ClientThreadManager singleton = null;
	
	private ClientThreadManager() {
		clients = new ArrayList<>();
	}
	
	public static ClientThreadManager getManager() {
		if(singleton == null) {
			singleton = new ClientThreadManager();
		}
		return singleton;
	}
	
	public boolean addNewClient(Socket socket) {
		ClientThread c = new ClientThread(socket,this);
		c.start();
		clients.add(c);
		return true;
	}


	public void deleteFromManager(ClientThread ct) {
		clients.remove(ct);
	}
	
	
	
}
