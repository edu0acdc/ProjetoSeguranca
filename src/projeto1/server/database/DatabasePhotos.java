package projeto1.server.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import projeto1.server.core.ClientInfo;
import projeto1.server.core.Photo;

public class DatabasePhotos {

	private static final String FOLDER_NAME = "server";
	
	
	private HashMap<Long, Photo> photos;
	private long next_id;
	private static DatabasePhotos singleton = null;
	
	
	private DatabasePhotos() {
		load();
		checkNextID();
		save();
	}
	
	private void checkNextID() {
		System.out.println("INFO: Checking next photo ID integrity");
		while(photos.containsKey(next_id)) {
			next_id++;
		}
		System.out.println("INFO: Next photo ID integrity checked");		
	}

	@SuppressWarnings("unchecked")
	private void load() {
		try {
			
			System.out.println("INFO: Loading photos database");
			FileInputStream fis = new FileInputStream(new File(FOLDER_NAME+"/photos_database.data"));
			ObjectInputStream ois = new ObjectInputStream(fis);
			photos = (HashMap<Long, Photo>) ois.readObject();
			next_id = photos.size();
			ois.close();
			
		}catch (IOException | ClassNotFoundException e) {
			System.out.println("ERROR: Not possible to load photos database, creating new one");
			photos = new HashMap<Long, Photo>();
			next_id = 0;
			return;
		}
		System.out.println("INFO: Photos database loaded");
	}
	

	public static DatabasePhotos getInstance() {
		if(singleton == null)
			singleton = new DatabasePhotos();
		return singleton;
	}
	
	public Photo getPhoto(long id) {
		return photos.get(id);
	}
	
	public synchronized long addPhoto(File f,ClientInfo client) {
		if(f == null || client == null) {
			System.out.println("ERROR: File or Owner can't be empty");
			return -1;
		}
		checkNextID();
		System.out.println("INFO: New photo added");
		Photo p = new Photo(f.getAbsolutePath(), next_id, client);
		photos.put(next_id,p);
		client.associatePhoto(p);
		checkNextID();
		save();
		return p.getID();
	}
	
	public boolean save() {
		try {
			FileOutputStream fos = new FileOutputStream(FOLDER_NAME+"/photos_database.data");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(photos);
			oos.close();
		}catch (IOException e) {
			System.out.println("ERROR: FATAL ERROR WHILE SAVING PHOTOS DATABASE");
			return false;
		}
		return true;
	}

	public boolean exists(Long id) {
		return photos.containsKey(id);
	}

	public boolean like(String username, Long id) {
		return photos.get(id).addLike(username);
	}
	
	
}
