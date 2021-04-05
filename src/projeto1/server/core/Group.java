package projeto1.server.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import projeto1.sharedCore.GroupKey;
import projeto1.sharedCore.MessageGroup;
import projeto1.sharedCore.MessageGroupEncrypted;

public class Group implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5336545197136146981L;
	private String owner;
	private String groupID;
	private List<String> active_members;
	private List<MessageGroup> msgs;
	private int counter;
	private HashMap<String, ArrayList<GroupKey>> keys;





	public Group(String groupID,String owner, GroupKey gk) {
		this.owner = owner;
		this.groupID = groupID;
		active_members = new ArrayList<>();
		active_members.add(owner);
		msgs = new ArrayList<MessageGroup>();
		this.counter = 0;
		keys = new HashMap<>();
		ArrayList<GroupKey> agk = new ArrayList<GroupKey>();
		agk.add(gk);
		keys.put(owner,agk);
	}

	public String getGroupID() {
		return groupID;
	}

	public String[] getMembers() {
		String[] aux = new String[active_members.size()];
		return active_members.toArray(aux);
	}

	public String getOwner() {
		return owner;
	}

	public boolean addMember(String username) {
		if(active_members.contains(username) || owner.contentEquals(username))
			return false;
		active_members.add(username);
		if(!keys.containsKey(username))
			keys.put(username,new ArrayList<>());
		return true;
	}

	public boolean removeMember(String username) {
		if(!active_members.contains(username))
			return false;
		active_members.remove(username);
		return true;
	}

	public boolean isMember(String username) {
		return active_members.contains(username) || owner.contentEquals(username);
	}

	@Override
	public String toString() {
		StringBuilder bob = new StringBuilder(groupID+"\nOwner:"+owner+"\nMembers:");
		for (String string : active_members) {
			bob.append(string+";");
		}
		return bob.toString();
	}

	public void addMessage(MessageGroup mg) {
		msgs.add(mg);
	}

	public MessageGroupEncrypted[] getHistory(String username) {
		ArrayList<GroupKey> ks = keys.get(username);

		List<MessageGroupEncrypted> msgs_aux = new ArrayList<>();
		for (MessageGroup msg : msgs) {
			int c = msg.getCounter(); 
			if(msg.alreadyRead(username)) {
				msg.readBy(username);
				for (GroupKey gp : ks) {
					if(gp.getIdentficador() == c) {
						msgs_aux.add(new MessageGroupEncrypted(msg, gp));
						break;
					}

				}

			}
		}
		MessageGroupEncrypted[] aux = new MessageGroupEncrypted[msgs_aux.size()];
		return msgs_aux.toArray(aux);	
	}

	public MessageGroupEncrypted[] collect(String username) {
		ArrayList<GroupKey> ks = keys.get(username);
		if(ks.size() == 0) {
			return new MessageGroupEncrypted[] {};
		}
		int lastUserCounter = ks.get(ks.size()-1).getIdentficador();

		List<MessageGroupEncrypted> msgs_aux = new ArrayList<>();
		for (MessageGroup msg : msgs) {
			int c = msg.getCounter(); 
			if(c <= lastUserCounter && !msg.alreadyRead(username)) {
				msg.readBy(username);
				for (GroupKey gp : ks) {
					if(gp.getIdentficador() == c) {
						msgs_aux.add(new MessageGroupEncrypted(msg, gp));
						break;
					}
				}
			}
		}
		MessageGroupEncrypted[] aux = new MessageGroupEncrypted[msgs_aux.size()];
		return msgs_aux.toArray(aux);		
	}


	public int getCounter() {
		return this.counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public boolean setNewKeys(GroupKey[] nkeys) {
		for (int i = 0; i < nkeys.length; i++) {
			ArrayList<GroupKey> ukeys = this.keys.get(nkeys[i].getUser());
			ukeys.add(nkeys[i]);
			keys.put(nkeys[i].getUser(),ukeys);
		}
		counter++;
		return true;
	}

	public boolean canRemoveMember(String username) {
		return active_members.contains(username) && !(username.contentEquals(owner));
	}

	public GroupKey getLastKey(String username) {
		ArrayList<GroupKey> ks = keys.get(username);
		return ks.get(ks.size()-1);
	}












}
