package projeto1.server.database;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import projeto1.server.core.ClientInfo;

public class DatabaseClients {

	
	
	private HashMap<String, ClientInfo> clients; // perigoso
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
	
	private void addFromTxt(String username,String nome_de_user,String password) {
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
			if(!c.getNomeUser().contentEquals(nome_de_user) || !c.getPassword().contentEquals(password)
					|| !c.getUsername().contentEquals(username)) {
				throw new IOException();
			}
		}catch (IOException | ClassNotFoundException e) {
			clients.put(username,new ClientInfo(username, password,nome_de_user));
			return;
		}
		clients.put(c.getUsername(),c);
		
	}

	private void load() {
		try {
			System.out.println("INFO: Loading clients database");
			Scanner sc = new Scanner(new File("server/users.txt"));
			clients = new HashMap<String, ClientInfo>();
			while(sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] aux = line.split(":");
				if(aux.length != 3) continue;
				addFromTxt(aux[0], aux[1],aux[2]);
			}
			sc.close();			
		}catch (IOException e) {
			System.out.println("Error while loading users.txt, creating new database");
			clients = new HashMap<String, ClientInfo>();
			return;
		}
		System.out.println("INFO: Client database loaded");
	}
	
	
	private boolean authenticate(String username,String password) {
		if(!clients.containsKey(username)) {
			return false;
		}
		
		return clients.get(username).getPassword().contentEquals(password);
		
	}
	
	public boolean createClient(String username,String password, String nome_de_user) {
		if(clients.containsKey(username)) {
			System.out.println("Username already in use");
			return false;
		}
		
		File folder = new File("server/"+username);
		if(!folder.mkdir()) {
			System.out.println("ERROR: Not possible to create personal folder ("+username+"), new client denied.");
			return false;
		}
		
		
		
		clients.put(username,new ClientInfo(username,password,nome_de_user));
		return save();
	}

	public synchronized boolean save() {
		try {
			FileWriter writer = new FileWriter(new File("server/users.txt"));
			for (Map.Entry<String,ClientInfo> entry : clients.entrySet()) {
				String username = entry.getKey();
				String nome = entry.getValue().getNomeUser();
				String password = entry.getValue().getPassword();			
				writer.append(username+":"+nome+":"+password+"\n");
				FileOutputStream fos = new FileOutputStream(new File("server/"+username+"/"+username+".info"));
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(entry.getValue());
				oos.close();
			}
			writer.close();
		}catch (IOException e) {
			System.out.println("Error while saving");
			return false;
		}
		return true;
	}
	
	public synchronized ClientInfo login(String username,String password) {
		if(authenticate(username, password)) {
			return clients.get(username);
		}
		
		return null;
				
	}

	public boolean checkAvailableUsername(String username) {
		return !clients.containsKey(username);
	}

	public boolean addFollower(String sender, String userID) {
		if(clients.get(userID).addFollower(sender)) {
			clients.get(sender).addFollowing(userID);
			save();
			return true;
		}
		return false;
	}

	public boolean removeFollower(String sender, String userID) {
		if(clients.get(sender).removeFollower(userID)) {
			clients.get(userID).removeFollowing(sender);
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
	
}
