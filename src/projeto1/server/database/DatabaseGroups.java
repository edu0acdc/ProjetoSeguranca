package projeto1.server.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import projeto1.server.core.Group;
import projeto1.server.core.SystemSeiTchizServer;
import projeto1.sharedCore.GroupKey;
import projeto1.sharedCore.MessageGroup;
import projeto1.sharedCore.MessageGroupEncrypted;

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
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.DECRYPT_MODE,new SecretKeySpec(SystemSeiTchizServer.getLoadedInstance().getPrivateKey().getEncoded(),"AES"));
			FileInputStream fis = new FileInputStream(new File(FOLDER_NAME+"/groups.data"));
			CipherInputStream cis = new CipherInputStream(fis, c);
			ObjectInputStream ois = new ObjectInputStream(cis);
			groups = (HashMap<String,Group>) ois.readObject();
			ois.close();
			cis.close();
			fis.close();
		}catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
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
		groups.get(groupID).addMember(username);
		save();
		return true;
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

	public synchronized boolean createGroup(String groupID, String owner, GroupKey gk) {
		if(groups.containsKey(groupID))
			return false;
		groups.put(groupID,new Group(groupID, owner,gk));
		save();
		return true;
	}

	public boolean existsGroup(String groupID) {
		return groups.containsKey(groupID);
	}

	public String[] getGroupsOfOwner(String owner) {
		List<String> aux = new ArrayList<>();
		for (Group g : groups.values()) {
			if(g.getOwner().contentEquals(owner)) {
				aux.add(g.toString()+"\n");
			}
		}
		String[] r = new String[aux.size()];
		return aux.toArray(r);
	}

	public String[] getGroupsOfUser(String member) {
		List<String> aux = new ArrayList<>();
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

	public void sendMessage(String groupID,MessageGroup mg) {
		groups.get(groupID).addMessage(mg);
		save();
	}

	public MessageGroupEncrypted[] collect(String sender, String groupID) {
		return groups.get(groupID).collect(sender);
	}

	public MessageGroupEncrypted[] history(String sender,String groupID) {
		return groups.get(groupID).getHistory(sender);
	}
	
	public void save() {
		try {
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.ENCRYPT_MODE, SystemSeiTchizServer.getLoadedInstance().getPrivateKey());
			FileOutputStream fos = new FileOutputStream(new File(FOLDER_NAME+"/groups.data"));
			CipherOutputStream cos = new CipherOutputStream(fos, c);
			ObjectOutputStream oos = new ObjectOutputStream(cos);
			oos.writeObject(groups);
			oos.close();
			cos.close();
			fos.close();
		}catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			System.out.println("ERROR: NOT POSSIBLE TO SAVE GROUPS DATABASE");
		}
	}

	public int getCounterOfGroup(String groupID) {
		if(!groups.containsKey(groupID))
			return -1;
		return groups.get(groupID).getCounter();
	}

	public boolean setNewKeys(String groupID, GroupKey[] keys) {
		if(!groups.containsKey(groupID))
			return false;
		groups.get(groupID).setNewKeys(keys);
		save();
		return true;
	}

	public boolean canRemoveMember(String username, String groupID) {
		if(!groups.containsKey(groupID))
			return false;
		return groups.get(groupID).canRemoveMember(username);
	}

	public GroupKey getLastKey(String username, String groupID) {
		if(!groups.containsKey(groupID))
			return null;
		return groups.get(groupID).getLastKey(username);
	}
	
	
	
	
	
}
