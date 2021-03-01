package projeto1.server.core;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientInfo implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2760533136576128947L;
	private String username;
	private String password;
	private String nome_user;
	private List<Long> photos_ids;
	private List<String> followers;
	private List<String> following;
	
	
	public ClientInfo(String username,String password,String nome_user) {
		this.followers = new ArrayList<String>();
		this.following = new ArrayList<String>();
		this.nome_user = nome_user;
		this.username = username;
		this.password = password;
		photos_ids = new ArrayList<Long>();
	}
	
	public boolean addFollower(String username) {
		if(followers.contains(username))
			return false;
		followers.add(username);
		return true;
	}
	
	public boolean addFollowing(String username) {
		if(following.contains(username))
			return false;
		following.add(username);
		return true;
	}
	
	public boolean removeFollower(String username) {
		return followers.remove(username);
	}
	
	public boolean removeFollowing(String username) {
		return following.remove(username);
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getUsername() {
		return username;
	}
	
	private void addPhoto(long id) {
		photos_ids.add(id);
		Collections.sort(photos_ids,Collections.reverseOrder());
	}
	
	public String getNomeUser() {
		return nome_user;
	}
	
	
	public void setPassword(String password) {
		this.password = password;
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
