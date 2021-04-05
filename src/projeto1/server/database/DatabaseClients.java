package projeto1.server.database;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import projeto1.server.core.ClientInfo;
import projeto1.server.core.SystemSeiTchizServer;

public class DatabaseClients {

	
	
	private HashMap<String, ClientInfo> clients; 
	private static DatabaseClients singleton = null;
	
	public static synchronized DatabaseClients getInstance() {
		if(singleton == null) {
			singleton = new DatabaseClients();
		}
		
		return singleton;
	}
	
	private DatabaseClients() {
		load();
		checkFolders();
	}

	
	private void checkFolders() {
		for (String	user : clients.keySet()) {
			File dir = new File("server/"+user);
			if(!dir.exists() || !dir.isDirectory()) {
				System.out.println("ERROR ("+user+"): PERSONAL FOLDER MISSING ");
				if(dir.mkdir()) {
					System.out.println("INFO ("+user+"): PERSONAL FOLDER RECREATED");
				}
				else
					System.out.println("ERROR ("+user+"): NOT POSSIBLE TO RECREATE PERSONAL FOLDER");
			}
		}
	}
	
	private void addFromTxt(String username,String certificado) {
		File dir = new File("server/"+username);
		ClientInfo c = null;
		if(!dir.exists() || !dir.isDirectory()) {
			System.out.println("ERROR ("+username+"): PERSONAL FOLDER MISSING ");
			if(dir.mkdir()) {
				System.out.println("INFO ("+username+"): PERSONAL FOLDER RECREATED");
			}
			else
				System.out.println("ERROR ("+username+"): NOT POSSIBLE TO RECREATE PERSONAL FOLDER");
		}
		try {
			FileInputStream fis = new FileInputStream("server/"+username+"/"+username+".info");
			ObjectInputStream ois = new ObjectInputStream(fis);
			c = (ClientInfo) ois.readObject();
			
			ois.close();
			if(!c.getUsername().contentEquals(username)) {
				throw new IOException();
			}
		}catch (IOException | ClassNotFoundException e) {
			clients.put(username,new ClientInfo(username,certificado));
			return;
		}
		clients.put(c.getUsername(),c);
		
	}

	private void load() {
		try {
			System.out.println("INFO: Loading clients database");
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.DECRYPT_MODE,new SecretKeySpec(SystemSeiTchizServer.getLoadedInstance().getPrivateKey().getEncoded(),"AES"));
			FileInputStream fis = new FileInputStream("server/users.txt");
			BufferedReader d = new BufferedReader(new InputStreamReader(new CipherInputStream(fis, c)));
			clients = new HashMap<>();
			String line = d.readLine();
			while(line != null) {
				String[] aux = line.split(":");
				if(aux.length != 2) continue;
				addFromTxt(aux[0], aux[1]);
				line = d.readLine();
			}
			d.close();			
			fis.close();
		}catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			System.out.println("Error while loading users.txt, creating new database");
			clients = new HashMap<>();
			return;
		}
		System.out.println("INFO: Client database loaded");
	}
	
		
	public synchronized ClientInfo createClient(String username,String certificado) {
		if(clients.containsKey(username)) {
			System.out.println("Username already in use");
			return null;
		}	
		
		ClientInfo c = new ClientInfo(username,certificado);
		if(c.getPublicKey() == null) {
			System.out.println("ERROR: NO PUBLIC KEY FOUND");
			return null;
		}
		File folder = new File("server/"+username);
		if(!folder.mkdir()) {
			System.out.println("ERROR: Not possible to create personal folder ("+username+"), new client denied.");
			clients.remove(username);
			return null;
		}
		clients.put(username,c);
		if(save()) {
			return clients.get(username);
		}
		else {
			System.out.println("ERROR: CAN NOT SAVE CLIENTS DATABASE");
			clients.remove(username);
			return null;
		}
	}

	public synchronized boolean save() {
		try {
			Cipher c = Cipher.getInstance("AES");
			Cipher c2 = Cipher.getInstance("AES");
			c.init(Cipher.ENCRYPT_MODE, SystemSeiTchizServer.getLoadedInstance().getPrivateKey());
			c2.init(Cipher.ENCRYPT_MODE, SystemSeiTchizServer.getLoadedInstance().getPrivateKey());
			FileOutputStream fosTXT = new FileOutputStream(new File("server/users.txt"));
			CipherOutputStream cosTXT = new CipherOutputStream(fosTXT, c);
			FileOutputStream fos = null;
			CipherOutputStream cosinfo = null;
			ObjectOutputStream oos = null;
			for (Map.Entry<String,ClientInfo> entry : clients.entrySet()) {
				String username = entry.getKey();
				String cert = entry.getValue().getCertificadoPath();
				String to_write = username+":"+cert;
				cosTXT.write(to_write.getBytes());
				fos = new FileOutputStream(new File("server/"+username+"/"+username+".info"));
				cosinfo = new CipherOutputStream(fos, c2);
				oos = new ObjectOutputStream(cosinfo);
				oos.writeObject(entry.getValue());
				oos.close();
				cosinfo.close();
				fos.close();
			}

			cosTXT.close();
			fosTXT.close();
		}catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
			System.out.println("Error while saving");
			return false;
		}
		return true;
	}
	

	public boolean checkAvailableUsername(String username) {
		return !clients.containsKey(username);
	}

	public boolean addFollower(String sender, String userID) {
		if(clients.get(sender).nowFollowing(userID)) {
			clients.get(userID).newFollower(sender);
			save();
			return true;
		}
		return false;
	}

	public boolean removeFollower(String sender, String userID) {
		if(clients.get(sender).removeFollowing(userID)) {
			clients.get(userID).removeFollower(sender);
			save();
			return true;
		}
		return false;
	}

	public String[] getFollowers(String sender) {
		return clients.get(sender).getFollowers();
	}

	public ClientInfo getClient(String sender) {
		return clients.get(sender);
	}

	public PublicKey getPublicKey(String username) {
		if(clients.containsKey(username)) {
			return clients.get(username).getPublicKey();
		}
		return null;
	}
	
}
