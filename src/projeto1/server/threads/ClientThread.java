package projeto1.server.threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import projeto1.server.core.ClientInfo;
import projeto1.server.exceptions.InvalidCertificateException;
import projeto1.server.handlers.RequestHandler;
import projeto1.sharedCore.LoginInfo;
import projeto1.sharedCore.Message;
import projeto1.sharedCore.MessagePacket;

public class ClientThread extends Thread {

	public static final String SERVER_NAME = "SeiTchizServer";
	public static final int TIMEOUT_ATTEMPTS = 10;
	public static final int TIME_TO_WAIT = 500; // milliseconds

	private Socket s;
	private RequestHandler rh;
	private ClientInfo client;
	private ClientThreadManager manager;
	private byte[] nonce;

	public ClientThread(Socket s,ClientThreadManager manager,byte[] nonce) {
		this.s = s;
		this.manager = manager;
		rh = RequestHandler.getInstance();
		this.nonce = nonce.clone();
	}

	@Override
	public void run() {
		try {
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

			//Login process
			if(!tryLogin(ois,oos)) killClient();
			//

			while(true) {
				MessagePacket packet = null;
				try {
					packet = (MessagePacket) ois.readObject();
				} catch (ClassNotFoundException e) {
					System.out.println("ERROR ("+client.getUsername()+") : NOT POSSIBLE READ PACKET");
					continue;
				}
				MessagePacket send = rh.handlePacket(packet,ois,oos);
				oos.writeObject(send);
			}

		} catch (IOException e) {
			String user = client == null ? "": client.getUsername();
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

		if(login_packet.getMsg() != Message.LOGIN) {
			//-----
			MessagePacket ps = new MessagePacket(Message.LOGIN_FAIL, new String[] {}, SERVER_NAME,new String[] {});
			ps.setINFO("Wrong login flow.");
			try {
				oos.writeObject(ps);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}



		String username = login_packet.getSender();
		if(!rh.existsUser(username)) {
			//Create
			try {
				MessagePacket need_cert_packet = new MessagePacket(Message.NEED_CERT_AND_SIGN, null, SERVER_NAME, null);
				need_cert_packet.setLoginInfo(new LoginInfo(nonce, null,null));
				oos.writeObject(need_cert_packet);

				MessagePacket register_packet = (MessagePacket) ois.readObject();
				if(register_packet.getMsg() != Message.TRY_REGISTER) {
					MessagePacket ps = new MessagePacket(Message.LOGIN_FAIL, new String[] {}, SERVER_NAME,new String[] {});
					ps.setINFO("Wrong login flow.");
					oos.writeObject(ps);
					return false;
				}

				try {
					client = rh.register(username,register_packet.getLoginInfo());
					if(client == null) {
						MessagePacket ps = new MessagePacket(Message.LOGIN_FAIL, new String[] {}, SERVER_NAME,new String[] {});
						ps.setINFO("Null client info.");
						oos.writeObject(ps);
						return false;
					}
					MessagePacket ps = new MessagePacket(Message.LOGIN_SUCCESS, new String[] {}, SERVER_NAME,new String[] {});
					ps.setINFO("Valid login info.");
					oos.writeObject(ps);
					return true;
				}catch (InvalidCertificateException e) {
					// ------------
					MessagePacket ps = new MessagePacket(Message.LOGIN_FAIL, new String[] {}, SERVER_NAME,new String[] {});
					ps.setINFO("Invalid login info.");
					oos.writeObject(ps);
				}	

				

			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
		}
		else {
			try {
				MessagePacket need_cert_packet = new MessagePacket(Message.NEED_SIGN, null, SERVER_NAME, null);
				need_cert_packet.setLoginInfo(new LoginInfo(nonce, null,null));
				oos.writeObject(need_cert_packet);
			} catch (IOException e1) {
				e1.printStackTrace();
				return false;
			}
			
			try {
				login_packet = (MessagePacket) ois.readObject();
				if(login_packet.getMsg() != Message.TRY_SIGN) {
					MessagePacket ps = new MessagePacket(Message.LOGIN_FAIL, new String[] {}, SERVER_NAME,new String[] {});
					ps.setINFO("Wrong login flow.");
					oos.writeObject(ps);
					return false;
				}
				//Login
				ClientInfo ci = rh.authenticate(username, login_packet.getLoginInfo());
				if(ci == null) {
					//---
					MessagePacket ps = new MessagePacket(Message.LOGIN_FAIL, new String[] {}, SERVER_NAME,new String[] {});
					ps.setINFO("Null client info.");
					oos.writeObject(ps);
					return false;
				}
				client = ci;
				MessagePacket ps = new MessagePacket(Message.LOGIN_SUCCESS, new String[] {}, SERVER_NAME,new String[] {});
				ps.setINFO("Valid login info.");
				oos.writeObject(ps);
				return true;
			}catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}			
		}		
		return true;
	}

	private void killClient() {
		System.out.println("Client Killed");
		try {
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		manager.deleteFromManager(this);
		this.interrupt();
	}
}
