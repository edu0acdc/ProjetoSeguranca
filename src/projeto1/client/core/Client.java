package projeto1.client.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.security.PrivateKey;
import java.security.Signature;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import projeto1.sharedCore.LoginInfo;
import projeto1.sharedCore.Message;
import projeto1.sharedCore.MessagePacket;
import projeto1.sharedCore.PartialFile;

public class Client extends Thread{

	private String ip;
	private String id;
	private int port;
	private SSLSocket s;
	private PacketProcessor pp;
	private CLI cli ;
	private PrivateKey private_key;
	private String cert_path;


	public Client(String ip, int port, String clientID, PrivateKey pk) {
		this.id = clientID;
		this.ip = ip;
		this.port = port;
		this.cli = new CLI(clientID);
		this.pp = new PacketProcessor();
		this.cert_path = "PubKeys/"+clientID+".cer";
		this.private_key = pk;
	}



	@Override
	public void run() {
		System.out.println("INFO: Trying to connect to "+ip+" at port "+port);
		try {
			s = (SSLSocket) (SSLSocketFactory.getDefault().createSocket(ip, port));
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if(s == null) {
			System.out.println("ERROR: SOCKET NOT READY");
			return;
		}

		System.out.println("INFO: Socket created with "+ip+":"+port);

		System.out.println("INFO: Running Client");
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

			if(!tryLogin(ois, oos)) {
				System.out.println("Login Failed");
				return;
			}
			cli.printWelcome();
			while(true) {
				MessagePacket to_send = cli.menu();
				if(to_send == null) {
					System.out.println("ERROR: NOT POSSIBLE TO EXECUTE COMMAND");
					continue;
				}	
				oos.writeObject(to_send);
				if(to_send.getMsg() == Message.POST) {
					post(oos,ois,to_send);
				}
				else if(to_send.getMsg() == Message.WALL) {
					wall(ois,to_send);
				}
				else {

					try {
						MessagePacket received = (MessagePacket) ois.readObject();
						while(received == null) {
							received = (MessagePacket) ois.readObject();
						}
						pp.processPacket(to_send.getMsg(),received);
					} catch (ClassNotFoundException e) {
						System.out.println("ERROR: NOT POSSIBLE TO RECEIVE PACKET");
					}
				}
			}


		} catch (IOException | ClassNotFoundException e) {
			System.out.println("ERROR: CONNECTION LOST");
		}
	}


	private void wall(ObjectInputStream ois,MessagePacket to_send) {
		try {
			PartialFile pf = null;
			do {
				pf = (PartialFile) ois.readObject();
				if(pf.getFilename().trim().length() == 0) {
					break;
				}
				File f = new File("client/"+id+"/wall/"+pf.getFilename());
				FileOutputStream fos = new FileOutputStream(f);

				while(true) {
					if(pf.getMsg() != Message.BYTES)
						break;
					fos.write(pf.getBytes(),0,pf.getLength());
					pf = (PartialFile) ois.readObject();
				}
				
				fos.close();
			}while(pf.getMsg() != Message.NO_MORE_FILES);



			MessagePacket received = (MessagePacket) ois.readObject();
			while(received == null) {
				received = (MessagePacket) ois.readObject();
			}
			pp.processPacket(to_send.getMsg(),received);

		}catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}



	}

	private boolean post(ObjectOutputStream oos, ObjectInputStream ois,MessagePacket to_send) throws IOException, ClassNotFoundException {
		String filename = to_send.getArgs()[0];
		File f = new File("client/"+id+"/photos/"+filename);
		
		
			FileInputStream fis = new FileInputStream(f);
			oos.writeObject(new PartialFile(new byte[] {},0,Message.NEW_FILE,filename));
			while(true) {
				byte[] bytes = new byte[1024];
				int bread = fis.read(bytes, 0, 1024);
				if(bread <= 0) {
					break;
				}
				oos.writeObject(new PartialFile(bytes, bread,Message.BYTES,filename));
			}
			oos.writeObject(new PartialFile(new byte[] {},0,Message.END_OF_FILE,filename));
			fis.close();

			MessagePacket received = (MessagePacket) ois.readObject();
			while(received == null) {
				received = (MessagePacket) ois.readObject();
			}
			pp.processPacket(to_send.getMsg(),received);
		

		return true;


	}

	private boolean tryLogin(ObjectInputStream ois,ObjectOutputStream oos) {
		MessagePacket login_response = null;
		try {
			oos.writeObject(new MessagePacket(Message.LOGIN,new String[] {id},id,new String[] {}));
			login_response = (MessagePacket) ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("ERROR: FATAL ERROR ON LOGIN");
			return false;
		}
		if(login_response.getMsg() == Message.NEED_CERT_AND_SIGN) {
			LoginInfo li = login_response.getLoginInfo();
			byte[] nonce = li.getNonce();
			Signature s = null;
			try {
				s = Signature.getInstance("MD5withRSA");
				s.initSign(private_key);
				s.update(nonce);	
				LoginInfo new_li = new LoginInfo(nonce, s.sign(), cert_path);
				MessagePacket try_register = new MessagePacket(Message.TRY_REGISTER, null, id, null);
				try_register.setLoginInfo(new_li);
				oos.writeObject(try_register);
			}catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			try {
				MessagePacket response = (MessagePacket) ois.readObject();
				System.out.println(Message.getDescription(response.getMsg()));
				return response.getMsg() == Message.LOGIN_SUCCESS;
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		else if(login_response.getMsg() == Message.NEED_SIGN) {
			LoginInfo li = login_response.getLoginInfo();
			byte[] nonce = li.getNonce();
			Signature s = null;
			try {
				s = Signature.getInstance("MD5withRSA");
				s.initSign(private_key);
				s.update(nonce);	
				LoginInfo new_li = new LoginInfo(nonce, s.sign(), null);
				MessagePacket try_sign = new MessagePacket(Message.TRY_SIGN, null, id, null);
				try_sign.setLoginInfo(new_li);
				oos.writeObject(try_sign);
			}catch (Exception e) {
				e.printStackTrace();
				return false;
			}
						
			try {
				MessagePacket response = (MessagePacket) ois.readObject();
				System.out.println(Message.getDescription(response.getMsg()));
				return response.getMsg() == Message.LOGIN_SUCCESS;
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
				return false;
			}
			
		}
		else {
			return false;
		}
	}

}
