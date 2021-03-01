package projeto1.client.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import projeto1.Message;
import projeto1.MessagePacket;
import projeto1.PartialFile;

public class Client extends Thread{

	private String ip;
	private String id;
	private String password;
	private int port;
	private Socket s;
	private PacketProcessor pp;
	private CLI cli ;

	public Client(String ip,int port, String client_id, String password) {
		this.password = password;
		this.id = client_id;
		this.ip = ip;
		this.port = port;
		this.cli = new CLI(id);
		this.pp = new PacketProcessor();
		try {
			System.out.println("INFO: Trying to connect to "+ip+" at port "+port);
			s = new Socket(ip, port);
		} catch (UnknownHostException e) {
			System.out.println("Error: Not possible to reach host");
			s = null;
		} catch (IOException e) {
			System.out.println("Error: Not possible to create socket");
			s = null;
		}


	}

	public Client(String ip,int port, String client_id) {
		this.password = null;
		this.id = client_id;
		this.ip = ip;
		this.port = port;
		this.cli = new CLI(id);
		this.pp = new PacketProcessor();
		try {
			System.out.println("INFO: Trying to connect to "+ip+" at port "+port);
			s = new Socket(ip, port);
		} catch (UnknownHostException e) {
			System.out.println("ERROR: Not possible to reach host");
			s = null;
		} catch (IOException e) {
			System.out.println("ERROR: Not possible to create socket");
			s = null;
		}



	}







	@Override
	public void run() {
		load();
		if(s == null) {
			System.out.println("ERROR: SOCKET NOT READY");
			return;
		}

		System.out.println("INFO: Socket created with "+ip+":"+port);

		System.out.println("INFO: Running Client");
		
		if(password == null) {
			password = cli.askPassword();
		}

		try {
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

			if(!tryLogin(ois, oos)) return;
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


		} catch (IOException e) {
			System.out.println("ERROR: CONNECTION LOST");
			e.printStackTrace();
		}
	}



	private void load() {
		System.out.println("INFO: Checking integrity of system");
		File folder_c = new File("client");
		if(!folder_c.exists() || !folder_c.isDirectory()) {
			System.out.println("ERROR: Client folder not found, creating new one");
			if(!folder_c.mkdir()) {
				System.out.println("ERROR: Critical error, not possible to create client folder, exiting with status 1");
				System.exit(1);
			}
			System.out.println("INFO: Client folder created");
		}

		File photos = new File("client/photos");
		if(!photos.exists() || !photos.isDirectory()) {
			System.out.println("ERROR: Photos folder not found, creating new one");
			if(!photos.mkdir()) {
				System.out.println("ERROR: Critical error, not possible to create photos folder, exiting with status 1");
				System.exit(1);
			}
			System.out.println("INFO: Photos folder created");
		}

		File wall = new File("client/wall");
		if(!wall.exists() || !wall.isDirectory()) {
			System.out.println("ERROR: Wall folder not found, creating new one");
			if(!wall.mkdir()) {
				System.out.println("ERROR: Wall error, not possible to create client folder, exiting with status 1");
				System.exit(1);
			}
			System.out.println("INFO: Wall folder created");
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
				File f = new File("client/wall/"+pf.getFilename());
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

	private boolean post(ObjectOutputStream oos, ObjectInputStream ois,MessagePacket to_send) {
		String filename = to_send.getArgs()[0];
		File f = new File("client/photos/"+filename);
		
		try {
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
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			return false;
		}

		return true;


	}

	private boolean tryLogin(ObjectInputStream ois,ObjectOutputStream oos) {
		MessagePacket login_response = null;
		try {
			oos.writeObject(new MessagePacket(Message.LOGIN,new String[] {id,password},id,new String[] {}));
			login_response = (MessagePacket) ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("ERROR: FATAL ERROR ON LOGIN");
			return false;
		}

		if(login_response.getMsg() == Message.LOGIN_SUCCESS) {
			System.out.println("INFO: LOGIN SUCCESSFULL"); 	 	
			return true;
		}
		else if(login_response.getMsg() == Message.NEED_USER_NAME) {
			String nome_de_user = cli.askNomeUser();
			while(nome_de_user.trim().length() == 0) {
				nome_de_user = cli.askNomeUser();
			}
			try {
				oos.writeObject(new MessagePacket(Message.USER_NAME,new String[] {nome_de_user},id,new String[] {})); //send
				
				MessagePacket response = (MessagePacket) ois.readObject();
				if(response.getMsg() == Message.LOGIN_SUCCESS) {
					System.out.println("INFO: LOGIN SUCCESSFULL"); 	 	
					return true;
				}
				else if(response.getMsg() == Message.LOGIN_FAIL) {
					System.out.println("ERROR: LOGIN FAILED");
					return false;
				}
				else {
					System.out.println("ERROR: FATAL ERROR");
					return false;
				}
			} catch (IOException | ClassNotFoundException e) {
				System.out.println("ERROR: CONNECTION LOST");
				e.printStackTrace();
				return false;
			}
		}
		else if(login_response.getMsg() == Message.LOGIN_FAIL) {
			System.out.println("ERROR: LOGIN FAILED");
		}
		else if (login_response.getMsg() == Message.LOGIN_WRONG_PASSWORD){
			System.out.println("ERROR: WRONG PASSWORD");
		}else {
			System.out.println("ERROR: FATAL ERROR");

		}
		return false;
	}

}
