package projeto1;

import java.io.Serializable;

public class PhotoInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8559432878792592603L;
	private String owner;
	private int likes;
	private String filename;
	private long id;
	
	public PhotoInfo(String owner,int likes,String filename,long id) {
		this.owner = owner;
		this.likes = likes;
		this.filename = filename;
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public int getLikes() {
		return likes;
	}
	
	@Override
	public String toString() {
		return "ID: "+id+"Photo of: "+owner+" | "+filename+" | Likes: "+likes;
	}
}
