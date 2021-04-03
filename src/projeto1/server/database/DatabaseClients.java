package projeto1.server.database;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import projeto1.server.core.ClientInfo;

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
			Scanner sc = new Scanner(new File("server/users.txt"));
			clients = new HashMap<>();
			while(sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] aux = line.split(":");
				if(aux.length != 2) continue;
				addFromTxt(aux[0], aux[1]);
			}
			sc.close();			
		}catch (IOException e) {
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
			System.out.println("NO PUBLIC KEY FOUND");
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
			System.out.println("CANT SAVE");
			clients.remove(username);
			return null;
		}
	}

	public synchronized boolean save() {
		try {
			FileWriter writer = new FileWriter(new File("server/users.txt"));
			for (Map.Entry<String,ClientInfo> entry : clients.entrySet()) {
				String username = entry.getKey();
				String cert = entry.getValue().getCertificadoPath();
				writer.append(username+":"+cert+"\n");
				FileOutputStream fos = new FileOutputStream(new File("server/"+username+"/"+username+".info"));
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(entry.getValue());
				oos.close();
			}
			writer.close();
		}catch (IOException e) {
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
