package projeto1.client.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import projeto1.sharedCore.GroupKey;
import projeto1.sharedCore.LoginInfo;
import projeto1.sharedCore.Message;
import projeto1.sharedCore.MessageGroup;
import projeto1.sharedCore.MessageGroupEncrypted;
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
			
				if(to_send.getMsg() == Message.POST) {
					oos.writeObject(to_send);
					post(oos,ois,to_send);
				}
				else if(to_send.getMsg() == Message.WALL) {
					oos.writeObject(to_send);
					wall(ois,to_send);
				}
				else if(to_send.getMsg() == Message.TRY_ADD_USER) {
					try {
						addUser(ois, oos, to_send);
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				else if(to_send.getMsg() == Message.NEW_GROUP) {
					try {
						createGroup(ois, oos, to_send);
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				else if(to_send.getMsg() == Message.TRY_REMOVE_USER) {
					try {
						removeUser(ois, oos, to_send);
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				else if(to_send.getMsg() == Message.TRY_SEND_MSG) {
					try {
						sendMsg(ois, oos, to_send);
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				else if(to_send.getMsg() == Message.COLLECT) {
					try {
						collect(ois, oos, to_send);
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				else if(to_send.getMsg() == Message.HISTORY) {
					try {
						history(ois, oos, to_send);
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				else {
					oos.writeObject(to_send);
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


	private void wall(ObjectInputStream ois,MessagePacket to_send) throws ClassNotFoundException, IOException {
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

	private void addUser(ObjectInputStream ois,ObjectOutputStream oos,MessagePacket to_send) throws NoSuchAlgorithmException, ClassNotFoundException, IOException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException {
		oos.writeObject(to_send);
		MessagePacket groupinfo = (MessagePacket) ois.readObject();
		if(groupinfo.getMsg() == Message.FAIL) {
			System.out.println(groupinfo.getINFO());
			return;
		}
		SecretKey sk = KeyGenerator.getInstance("AES").generateKey();
		
		String[] users = (String[]) groupinfo.getResults()[0];
		int counter = (int) groupinfo.getResults()[1];
		counter++;
		GroupKey[] new_keys = new GroupKey[users.length];
		for (int i = 0; i < users.length; i++) {
			PublicKey pub = SecurityChecker.getInstance().getCertificate(users[i]).getPublicKey();
			Cipher c = Cipher.getInstance("RSA");	
			c.init(Cipher.WRAP_MODE, pub);
			byte[] wrappedKey = c.wrap(sk);
			new_keys[i] = new GroupKey(users[i], counter, wrappedKey);
		}
		
		to_send = new MessagePacket(Message.ADD_USER, null, id, null);
		to_send.addResults(new_keys);
		oos.writeObject(to_send);
		
		MessagePacket received = (MessagePacket) ois.readObject();
		if(received.getMsg() == Message.SUCCESS) {
			System.out.println("User added");
		}
		else {
			System.out.println("Error while adding user to group");
			System.out.println("INFO:"+received.getINFO());
		}
	}

	private void removeUser(ObjectInputStream ois,ObjectOutputStream oos,MessagePacket to_send) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException {
		oos.writeObject(to_send);
		MessagePacket groupinfo = (MessagePacket) ois.readObject();
		if(groupinfo.getMsg() == Message.FAIL) {
			System.out.println(groupinfo.getINFO());
			return;
		}
		SecretKey sk = KeyGenerator.getInstance("AES").generateKey();
		String[] users = (String[]) groupinfo.getResults()[0];
		int counter = (int) groupinfo.getResults()[1];
		counter++;
		GroupKey[] new_keys = new GroupKey[users.length];
		for (int i = 0; i < users.length; i++) {
			PublicKey pub = SecurityChecker.getInstance().getCertificate(users[i]).getPublicKey();
			Cipher c = Cipher.getInstance("RSA");	
			c.init(Cipher.WRAP_MODE, pub);
			byte[] wrappedKey = c.wrap(sk);
			new_keys[i] = new GroupKey(users[i], counter, wrappedKey);
		}
		
		to_send = new MessagePacket(Message.REMOVE_USER, null, id, null);
		to_send.addResults(new_keys);
		oos.writeObject(to_send);
		
		MessagePacket received = (MessagePacket) ois.readObject();
		if(received.getMsg() == Message.SUCCESS) {
			System.out.println("User removed");
		}
		else {
			System.out.println("Error while removing user of group");
			System.out.println("INFO:"+received.getINFO());
		}
	}

	private void createGroup(ObjectInputStream ois,ObjectOutputStream oos,MessagePacket to_send) throws NoSuchAlgorithmException, InvalidKeyException, 
	NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException, ClassNotFoundException {
		SecretKey sk = KeyGenerator.getInstance("AES").generateKey();
		Cipher c = Cipher.getInstance("RSA");
		PublicKey pubk = SecurityChecker.getInstance().getCertificate(this.id).getPublicKey();
		c.init(Cipher.WRAP_MODE, pubk);
		byte[] wrappedKey = c.wrap(sk);
		GroupKey gk = new GroupKey(id, 0, wrappedKey);
		to_send.addResults(gk);
		oos.writeObject(to_send);	
		MessagePacket received = (MessagePacket) ois.readObject();
		if(received.getMsg() == Message.SUCCESS) {
			System.out.println("Group created");
		}
		else {
			System.out.println("Erro while creating group");
			System.out.println("INFO:"+received.getINFO());
		}
	}

	private void sendMsg(ObjectInputStream ois,ObjectOutputStream oos,MessagePacket to_send) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, ClassNotFoundException, IllegalBlockSizeException, BadPaddingException {
		String data = to_send.getArgs()[1];
		oos.writeObject(new MessagePacket(Message.TRY_SEND_MSG, new String[] {to_send.getArgs()[0]}, id, null));
		
		MessagePacket mpk = (MessagePacket) ois.readObject();
		if(mpk.getMsg() == Message.FAIL) {
			System.out.println(mpk.getINFO());
			return;
		}
		
		GroupKey gp = (GroupKey) mpk.getResults()[0];
		int counter = (int) mpk.getResults()[1];
		Cipher c = Cipher.getInstance("RSA");
		c.init(Cipher.UNWRAP_MODE, private_key);
		Key key = c.unwrap(gp.getWrappedKey(),"AES", Cipher.SECRET_KEY);
		
		c = Cipher.getInstance("AES");
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encMsg = c.doFinal(data.getBytes());
		
		MessagePacket msg = new MessagePacket(Message.MSG,null,id,null);
		msg.addResults(new MessageGroup(id, encMsg, counter));
		oos.writeObject(msg);
		
		MessagePacket received = (MessagePacket) ois.readObject();
		if(received.getMsg() == Message.SUCCESS) {
			System.out.println(received.getINFO());
		}
		else {
			System.out.println("Erro while creating group");
			System.out.println("INFO:"+received.getINFO());
		}
		
	}

	private void collect(ObjectInputStream ois,ObjectOutputStream oos,MessagePacket to_send) throws ClassNotFoundException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		oos.writeObject(to_send);
		MessagePacket collect = (MessagePacket) ois.readObject();
		
		if(collect.getMsg() == Message.FAIL) {
			System.out.println(collect.getINFO());
			return;
		}		
		MessageGroupEncrypted[] mges = (MessageGroupEncrypted[]) collect.getResults()[0];
		for (int i = 0; i < mges.length; i++) {
			MessageGroupEncrypted mge = mges[i];
			Cipher c = Cipher.getInstance("RSA");
			GroupKey gp = mge.getGroupKey();
			c.init(Cipher.UNWRAP_MODE, private_key);
			Key key = c.unwrap(gp.getWrappedKey(),"AES", Cipher.SECRET_KEY);
			c = Cipher.getInstance("AES");
			c.init(Cipher.DECRYPT_MODE, key);
			String data = new String(c.doFinal(mge.getMg().getEncodedMsg()));
			System.out.println(mge.getMg().getSender()+"->"+data);
		}
	}

	private void history(ObjectInputStream ois,ObjectOutputStream oos,MessagePacket to_send) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException {
		oos.writeObject(to_send);
		MessagePacket history = (MessagePacket) ois.readObject();
		
		if(history.getMsg() == Message.FAIL) {
			System.out.println(history.getINFO());
			return;
		}		
		MessageGroupEncrypted[] mges = (MessageGroupEncrypted[]) history.getResults()[0];
		for (int i = 0; i < mges.length; i++) {
			MessageGroupEncrypted mge = mges[i];
			Cipher c = Cipher.getInstance("RSA");
			GroupKey gp = mge.getGroupKey();
			c.init(Cipher.UNWRAP_MODE, private_key);
			Key key = c.unwrap(gp.getWrappedKey(),"AES", Cipher.SECRET_KEY);
			c = Cipher.getInstance("AES");
			c.init(Cipher.DECRYPT_MODE, key);
			String data = new String(c.doFinal(mge.getMg().getEncodedMsg()));
			System.out.println(mge.getMg().getSender()+"->"+data);
		}
	}


}
