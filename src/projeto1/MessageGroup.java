package projeto1;

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
	
	public MessageGroup(String sender,String msg) {
		this.sender = sender;
		this.msg = msg;
		seen_by = new ArrayList<String>();
		seen_by.add(sender);
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
	
	@Override
	public String toString() {
		return sender+":"+msg;
	}
	
}
