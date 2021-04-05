package projeto1.sharedCore;

import java.io.Serializable;

public class GroupKey implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7594469148776825507L;
	private String user;
	private int identficador;
	private byte[] wrappedKey;
	
	public GroupKey(String user, int identficador,byte[] wrappedKey) {
		this.user = user;
		this.identficador = identficador;
		this.wrappedKey = wrappedKey.clone();
	}
	
	public byte[] getWrappedKey() {
		return wrappedKey;
	}
	
	public String getUser() {
		return user;
	}
	
	public int getIdentficador() {
		return identficador;
	}
	
	
	
}
