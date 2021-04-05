package projeto1.server.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import projeto1.sharedCore.MessageGroup;

public class MessageBox implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6777694271220875120L;
	private List<MessageGroup> to_read;
	private List<MessageGroup> read;
	private String groupID;
	private String username;
	
	public MessageBox(String groupID,String username) {
		this.groupID = groupID;
		this.username = username;
		to_read = new ArrayList<>();
		read = new ArrayList<>();
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getGroupID() {
		return groupID;
	}
	
	
	public void addMessage(MessageGroup msg) {
		to_read.add(msg);
	}
	
	public MessageGroup[] getHistory() {
		MessageGroup[] aux = new MessageGroup[read.size()];
		return read.toArray(aux);
	}
	

	public MessageGroup[] collect() {
		MessageGroup[] aux = new MessageGroup[to_read.size()];
		for (int i = 0; i < aux.length; i++) {
			aux[i] = to_read.get(i);
			read.add(to_read.get(i));
		}
		to_read = new ArrayList<>();
		return aux;
	}

	public void addOwnMessage(MessageGroup m) {
		read.add(m);		
	}
	
}
