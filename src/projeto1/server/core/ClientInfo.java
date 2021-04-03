package projeto1.server.core;
import java.io.FileInputStream;
import java.io.Serializable;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientInfo implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 2760533136576128947L;
	private String username;
	private String certificadoPath;
	private PublicKey publicKey;
	private List<Long> photos_ids;
	private List<String> followers;
	private List<String> following;


	public ClientInfo(String username,String certificado) {
		this.followers = new ArrayList<>();
		this.following = new ArrayList<>();
		this.certificadoPath = certificado;
		this.username = username;
		photos_ids = new ArrayList<>();

		loadPublicKey();
	}

	private void loadPublicKey() {
		try {
			FileInputStream kfile = new FileInputStream("myKeys"); //keystore
			KeyStore kstore = KeyStore.getInstance("JCEKS");
			kstore.load(kfile, "123456".toCharArray()); //password da keystore
			Certificate cert = kstore.getCertificate(username); //alias da keypair
			publicKey = cert.getPublicKey();
		}catch (Exception e) {
			publicKey = null;
		}
	}

	public boolean nowFollowing(String username) {
		if(following.contains(username))
			return false;
		following.add(username);
		return true;
	}

	public boolean newFollower(String username) {
		if(followers.contains(username))
			return false;
		followers.add(username);
		return true;
	}

	public boolean removeFollower(String username) {
		return followers.remove(username);
	}

	public boolean removeFollowing(String username) {
		return following.remove(username);
	}

	public String getUsername() {
		return username;
	}

	private void addPhoto(long id) {
		photos_ids.add(id);
		Collections.sort(photos_ids,Collections.reverseOrder());
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public String getCertificadoPath() {
		return certificadoPath;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void associatePhoto(Photo p) {
		addPhoto(p.getID());
	}

	public Long[] getPhotosID() {
		Long[] aux = new Long[photos_ids.size()];
		return photos_ids.toArray(aux);
	}


	public List<Long> getNPhotosID(int n){
		List<Long> aux = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			if(i >= photos_ids.size())
				break;
			aux.add(photos_ids.get(i));
		}
		return aux;
	}

	public String[] getFollowers() {
		String[] aux = new String[followers.size()];
		return followers.toArray(aux);
	}

	public boolean isFollowing(String username) {
		return following.contains(username);
	}

	public boolean isFollowedBy(String username) {
		return followers.contains(username);
	}

	public String[] getFollowing() {
		String[] aux = new String[following.size()];
		return following.toArray(aux);
	}


}
