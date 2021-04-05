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
	private byte[] encoded_msg;
	private int counter;
	private List<String> readBy;
	
	public MessageGroup(String sender,byte[] msg,int counter) {
		this.sender = sender;
		this.encoded_msg = msg;
		this.counter = counter;
		readBy = new ArrayList<>();
		readBy.add(sender);
	}
	
	public void readBy(String username) {
		readBy.add(username);
	}
	
	public boolean alreadyRead(String username) {
		return readBy.contains(username);
	}
	
	public int getCounter() {
		return counter;
	}
	
	public String getSender() {
		return sender;
	}
	
	public byte[] getEncodedMsg() {
		return encoded_msg;
	}
	
	
}
