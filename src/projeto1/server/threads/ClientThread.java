package projeto1.server.threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import projeto1.Message;
import projeto1.MessagePacket;
import projeto1.server.core.ClientInfo;
import projeto1.server.handlers.RequestHandler;

public class ClientThread extends Thread {


	public static final int TIMEOUT_ATTEMPTS = 10;
	public static final int TIME_TO_WAIT = 500; // milliseconds

	private Socket s;
	private RequestHandler rh;
	private ClientInfo client;
	private ClientThreadManager manager;

	public ClientThread(Socket s,ClientThreadManager manager) {
		this.s = s;
		this.manager = manager;
		rh = RequestHandler.getInstance();
	}

	@Override
	public void run() {

		try {
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

			
			//Login process
			if(!tryLogin(ois,oos)) return;
			//
			
			while(true) {
				MessagePacket packet = null;
				try {
					packet = (MessagePacket) ois.readObject();
				} catch (ClassNotFoundException e) {
					System.out.println("ERROR ("+client.getNomeUser()+") : NOT POSSIBLE READ PACKET");
					continue;
				}
				MessagePacket send = rh.handlePacket(packet,ois,oos);
				oos.writeObject(send);
			}
			








		} catch (IOException e) {
			String user = client.getNomeUser() == null ? "":client.getNomeUser();
			System.out.println("ERROR ("+user+") : CONNECTION LOST ");
		}

		manager.deleteFromManager(this);



	}

	private boolean tryLogin(ObjectInputStream ois, ObjectOutputStream oos) {
		MessagePacket login_packet = null;
		try {
			login_packet = (MessagePacket) ois.readObject();
		} catch (ClassNotFoundException | IOException e1) {
			System.out.println("ERROR: IMPOSSIBLE TO READ LOGIN PACKET");
			return false;
		}
		String[] args = login_packet.getArgs();
		String username = args[0];
		String password = args[1];

		try {
			client = rh.authenticate(username,password);
			if(client == null) {
				System.out.println("ERROR: "+username+" LOGIN FAILED");
				oos.writeObject(new MessagePacket(Message.LOGIN_FAIL, new String[] {},username,new String[] {}));
				return false;
			}
			System.out.println("INFO: "+username+" login success");
			oos.writeObject(new MessagePacket(Message.LOGIN_SUCCESS, new String[] {},username,new String[] {}));
		}catch (IOException e) {
			System.out.println("ERROR: NOT POSSIBLE TO ANSWER "+username);
			return false;
		}
		return true;
	}


}
