package projeto1.server.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

import projeto1.MessageGroup;

public class Group implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5336545197136146981L;
	private String owner;
	private String groupID;
	private HashMap<String, MessageBox> msg_box;
	
	public Group(String groupID,String owner) {
		this.owner = owner;
		this.groupID = groupID;
		msg_box = new HashMap<String, MessageBox>();
		msg_box.put(owner,new MessageBox(groupID,owner));
	}
	
	public String getGroupID() {
		return groupID;
	}
	
	public String[] getMembers() {
		String[] aux = new String[msg_box.keySet().size()];
		return msg_box.keySet().toArray(aux);
	}
	
	public String getOwner() {
		return owner;
	}
	
	public boolean addMember(String username) {
		if(msg_box.containsKey(username))
			return false;
		msg_box.put(username,new MessageBox(groupID, username));
		return true;
	}
	
	public boolean removeMember(String username) {
		if(!msg_box.containsKey(username))
			return false;
		msg_box.remove(username);
		return true;
	}

	public boolean isMember(String username) {
		return msg_box.containsKey(username);
	}
	
	@Override
	public String toString() {
		StringBuilder bob = new StringBuilder("Owner:"+owner+"\nMembers:");
		for (String string : msg_box.keySet()) {
			bob.append(string+";");
		}
		return bob.toString();
	}
	
	public void addMessage(MessageGroup m) {
		for (Entry<String, MessageBox> entry : msg_box.entrySet()) {
			if(!entry.getKey().contentEquals(m.getSender())) {
				entry.getValue().addMessage(m);
			}
			else {
				entry.getValue().addOwnMessage(m);
			}
		}
	}
	
	public MessageGroup[] getHistory(String username) {
		return msg_box.get(username).getHistory();
	}

	public MessageGroup[] collect(String username) {
		return msg_box.get(username).collect();	
	}  
	
	
	
	
	
	
	
	
	
	
	
	
}
