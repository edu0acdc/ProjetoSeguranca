package projeto1.server.core;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import projeto1.PhotoInfo;

public class Photo implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1408403643543122773L;
	private long id;
	private List<String> likes;
	private ClientInfo owner;
	private String path;
	
	public Photo(String path,long id,ClientInfo owner) {
		this.path = path;
		this.id = id;
		this.owner = owner;
		this.likes = new ArrayList<String>();
	}
	
	public File getFile() {
		return new File(path);
	}
	
		
	public boolean addLike(String clientID) {
		if(likes.contains(clientID)) {
			return false;
		}
		likes.add(clientID);
		return true;
	}
	
	public boolean removeLike(String clientID) {
		if(!likes.contains(clientID)) {
			return false;
		}
		return likes.remove(clientID);
	}
	
	
	public ClientInfo getOwner() {
		return owner;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Photo)) {
			return false;
		}
		
		Photo p = (Photo) obj;
		
		return this.id == p.id;
	}

	public long getID() {
		return id;
	}
	
	public PhotoInfo toPhotoInfo() {
		return new PhotoInfo(owner.getNomeUser(), likes.size(), new File(path).getName(),id);
	}
	
	
	
	
	

}
