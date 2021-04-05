package projeto1.server.core;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import projeto1.sharedCore.PhotoInfo;

public class Photo implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1408403643543122773L;
	private long id;
	private byte[] hash;
	private List<String> likes;
	private ClientInfo owner;
	private String path;

	public Photo(String path,long id,ClientInfo owner) {
		this.path = path;
		this.id = id;
		this.owner = owner;
		this.likes = new ArrayList<String>();
		calculateHash();
	}

	private void calculateHash() {
		File f = new File(path);
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] bytes = Files.readAllBytes(f.toPath());
			hash = md.digest(bytes);
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
			hash = new byte[] {};
		}
	}

	public boolean isValid() throws NoSuchAlgorithmException, IOException {
		File f = new File(path);
		MessageDigest md = MessageDigest.getInstance("SHA");
		byte[] bytes = Files.readAllBytes(f.toPath());
		byte[] new_hash = md.digest(bytes);
		return MessageDigest.isEqual(this.hash, new_hash);
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
		return new PhotoInfo(owner.getUsername(), likes.size(), new File(path).getName(),id);
	}






}
