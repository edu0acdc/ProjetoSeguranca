package projeto1.server.handlers;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import projeto1.MessagePacket;
import projeto1.server.core.ClientInfo;
import projeto1.server.database.DatabaseClients;

public class RequestHandler {

	private static RequestHandler singleton = null;
	private DatabaseClients database = DatabaseClients.getInstance();
	private PacketProcessorServer pps;
	
	private RequestHandler() {
		pps = new PacketProcessorServer();
	}
	
	public static RequestHandler getInstance() {
		if(singleton == null) {
			singleton = new RequestHandler();
		}
		return singleton;
	}

	public ClientInfo authenticate(String username, String password) {
		return database.login(username, password);
	}

	public MessagePacket handlePacket(MessagePacket packet,ObjectInputStream ois,ObjectOutputStream oos) {
		return pps.processPacket(packet,ois,oos);
	}
	
}
