package projeto1.server.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import projeto1.server.core.ClientInfo;
import projeto1.server.core.Photo;
import projeto1.server.core.SystemSeiTchizServer;

public class DatabasePhotos {

	private static final String FOLDER_NAME = "server";
	
	
	private HashMap<Long, Photo> photos;
	private long next_id;
	private static DatabasePhotos singleton = null;
	
	
	private DatabasePhotos() {
		load();
		checkNextID();
	}
	
	private void checkNextID() {
		while(photos.containsKey(next_id)) {
			next_id++;
		}
		System.out.println("INFO: Next photo ID integrity checked");		
	}

	@SuppressWarnings("unchecked")
	private void load() {
		try {
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.DECRYPT_MODE,new SecretKeySpec(SystemSeiTchizServer.getLoadedInstance().getPrivateKey().getEncoded(),"AES"));
			FileInputStream fis = new FileInputStream(FOLDER_NAME+"/photos_database.cif");
			CipherInputStream cis = new CipherInputStream(fis, c);
			ObjectInputStream ois = new ObjectInputStream(cis);
			photos = (HashMap<Long, Photo>) ois.readObject();
			ois.close();
			cis.close();
			fis.close();
		}catch (Exception e) {
			System.out.println("ERROR: Not possible to load photos database, creating new one");
			photos = new HashMap<>();
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
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.ENCRYPT_MODE, SystemSeiTchizServer.getLoadedInstance().getPrivateKey());
			FileOutputStream fos = new FileOutputStream(FOLDER_NAME+"/photos_database.cif");
			CipherOutputStream cos = new CipherOutputStream(fos, c);
			ObjectOutputStream oos = new ObjectOutputStream(cos);
			oos.writeObject(photos);
			oos.close();
			fos.close();
			cos.close();
		}catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
			System.out.println("ERROR: FATAL ERROR WHILE SAVING PHOTOS DATABASE");
			e.printStackTrace();
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
