package projeto1.server.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import projeto1.MessageGroup;

public class Group implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5336545197136146981L;
	private String owner;
	private String groupID;
	private List<MessageGroup> open_msgs;
	private List<MessageGroup> history;
	private List<String> members;
	
	
	public Group(String groupID,String owner) {
		this.owner = owner;
		this.groupID = groupID;
		open_msgs = new ArrayList<MessageGroup>();
		history = new ArrayList<MessageGroup>();
		members = new ArrayList<String>();
	}
	
	public String getGroupID() {
		return groupID;
	}
	
	public String[] getMembers() {
		String[] aux = new String[members.size()];
		return members.toArray(aux);
	}
	
	public String getOwner() {
		return owner;
	}
	
	public boolean addMember(String username) {
		if(members.contains(username) || owner.contentEquals(username))
			return false;
		members.add(username);
		return true;
	}
	
	public boolean removeMember(String username) {
		if(!members.contains(username))
			return false;
		members.remove(username);
		checkHistory();
		return true;
	}

	public boolean isMember(String username) {
		return members.contains(username) || owner.contentEquals(username);
	}
	
	@Override
	public String toString() {
		StringBuilder bob = new StringBuilder(groupID+"\nOwner:"+owner+"\nMembers:");
		for (String string : members) {
			bob.append(string+";");
		}
		return bob.toString();
	}
	
	public void addMessage(String sender,String msg) {
		String[] readers = new String[members.size()+1];
		members.toArray(readers);
		readers[readers.length-1] = owner;
		open_msgs.add(new MessageGroup(sender, msg, readers));
		checkHistory();
	}
	
	public MessageGroup[] getHistory(String username) {
		List<MessageGroup> aux1 = new ArrayList<>();
		for (MessageGroup msg : history) {
			if(msg.canBeSeenBy(username)) {
				aux1.add(msg);
			}
		}
		MessageGroup[] aux = new MessageGroup[aux1.size()];
		return aux1.toArray(aux);
	}

	public MessageGroup[] collect(String username) {
		List<MessageGroup> aux1 = new ArrayList<>();
		for (MessageGroup msg : open_msgs) {
			if(!msg.alreadySeen(username) && msg.canBeSeenBy(username)) {
				aux1.add(msg);
				msg.newView(username);
			}
		}
		checkHistory();
		MessageGroup[] aux = new MessageGroup[aux1.size()];
		return aux1.toArray(aux);
	}

	private void checkHistory() {
		boolean to_move;
		List<MessageGroup> moving = new ArrayList<>();
		
		for (MessageGroup msg : open_msgs) {
			to_move = msg.alreadySeen(owner);
			for (String user : members) {
				to_move = to_move && msg.alreadySeen(user);
			}
			if(to_move) {
				moving.add(msg);
			}				
		}
		
		open_msgs.removeAll(moving);
		history.addAll(moving);
	}  
	
	
	
	
	
	
	
	
	
	
	
	
}
