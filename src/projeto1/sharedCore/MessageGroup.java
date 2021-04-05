package projeto1.sharedCore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MessageGroup implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8688686278566963597L;
	private String sender;
	private String msg;
	private List<String> seen_by;
	private String[] readers;
	
	public MessageGroup(String sender,String msg,String[] readers) {
		this.sender = sender;
		this.msg = msg;
		seen_by = new ArrayList<String>();
		seen_by.add(sender);
		this.readers = readers.clone();
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public void newView(String username) {
		seen_by.add(username);
	}
	
	public boolean alreadySeen(String username) {
		return seen_by.contains(username);
	}
	
	public boolean isHistory() {
		for (int i = 0; i < readers.length; i++) {
			if(!seen_by.contains(readers[i])) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		return sender+":"+msg;
	}

	public boolean canBeSeenBy(String username) {
		if(sender.contentEquals(username))
			return true;
		for (int i = 0; i < readers.length; i++) {
			if(readers[i].contentEquals(username))
				return true;
		}
		return false;
	}
	
}
