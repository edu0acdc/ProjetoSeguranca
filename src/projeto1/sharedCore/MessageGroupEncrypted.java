package projeto1.sharedCore;

import java.io.Serializable;

public class MessageGroupEncrypted implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7166383026726684733L;
	private MessageGroup mg;
	private GroupKey gk;
	
	public MessageGroupEncrypted(MessageGroup mg,GroupKey gk) {
		this.mg = mg;
		this.gk = gk;
	}
	
	public GroupKey getGroupKey() {
		return gk;
	}
	
	public MessageGroup getMg() {
		return mg;
	}
}
