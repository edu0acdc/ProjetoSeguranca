package projeto1.server.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import projeto1.MessageGroup;
import projeto1.server.core.Group;

public class DatabaseGroups {

	private static final String FOLDER_NAME = "server";
	private HashMap<String, Group> groups;
	private static DatabaseGroups singleton = null;
	
	private DatabaseGroups() {
		load();
	}
	
	@SuppressWarnings("unchecked")
	private void load() {
		try {			
			System.out.println("INFO: Loading groups database");
			FileInputStream fis = new FileInputStream(new File(FOLDER_NAME+"/groups.data"));
			ObjectInputStream ois = new ObjectInputStream(fis);
			groups = (HashMap<String,Group>) ois.readObject();
			ois.close();
			
		}catch (IOException | ClassNotFoundException e) {
			System.out.println("ERROR: Not possible to load groups database, creating new one");
			groups = new HashMap<String, Group>();
			return;
		}
		System.out.println("INFO: Groups database loaded");
	}
	
	public static DatabaseGroups getInstance() {
		if(singleton == null) {
			singleton = new DatabaseGroups();
		}
		return singleton;
	}
	
	public boolean isMember(String username,String groupID) {
		if(!groups.containsKey(groupID))
			return false;
		return groups.get(groupID).isMember(username);
	}
	
	public boolean addMember(String username,String groupID) {
		if(!groups.containsKey(groupID))
			return false;
		save();
		return groups.get(groupID).addMember(username);
	}
	
	public boolean removeMember(String username,String groupID) {
		if(!groups.containsKey(groupID))
			return false;
		return groups.get(groupID).removeMember(username);
	}
	
	public String[] getMembers(String groupID) {
		if(!groups.containsKey(groupID))
			return null;
		return groups.get(groupID).getMembers();
	}
	
	public boolean isOwner(String username,String groupID) {
		if(!groups.containsKey(groupID))
			return false;
		return groups.get(groupID).getOwner().contentEquals(username);
	}

	public synchronized boolean createGroup(String groupID, String owner) {
		if(groups.containsKey(groupID))
			return false;
		groups.put(groupID,new Group(groupID, owner));
		save();
		return true;
	}

	public boolean existsGroup(String groupID) {
		return groups.containsKey(groupID);
	}

	public String[] getGroupsOfOwner(String owner) {
		List<String> aux = new ArrayList<String>();
		for (Group g : groups.values()) {
			if(g.getOwner().contentEquals(owner)) {
				aux.add(g.toString()+"\n");
			}
		}
		String[] r = new String[aux.size()];
		return aux.toArray(r);
	}

	public String[] getGroupsOfUser(String member) {
		List<String> aux = new ArrayList<String>();
		for (Group g : groups.values()) {
			if(g.isMember(member)) {
				aux.add(g.toString()+"\n");
			}
		}
		String[] r = new String[aux.size()];
		return aux.toArray(r);
	}

	public String getGroupString(String groupID) {
		return groups.get(groupID).toString();
	}

	public void sendMessage(String sender, String msg,String groupID) {
		groups.get(groupID).addMessage(sender, msg);
		save();
	}

	public MessageGroup[] collect(String sender, String groupID) {
		return groups.get(groupID).collect(sender);
	}

	public MessageGroup[] history(String sender,String groupID) {
		return groups.get(groupID).getHistory(sender);
	}
	
	public void save() {
		try {
			FileOutputStream fos = new FileOutputStream(new File(FOLDER_NAME+"/groups.data"));
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(groups);
			oos.close();
		}catch (IOException e) {
			System.out.println("ERROR: NOT POSSIBLE TO SAVE GROUPS DATABASE");
		}
	}
	
	
	
	
	
}
