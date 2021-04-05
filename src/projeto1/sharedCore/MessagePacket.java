package projeto1.sharedCore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MessagePacket implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5398839429323169566L;
	private int msg;
	private String[] args;
	private String[] files;
	private List<Object> results;
	private String info;
	private String sender;
	private LoginInfo login_info;
	
	public MessagePacket(int message,String[] args,String username,String[] files) {
		this.msg = message;
		if(args != null)
			this.args = args.clone();
		this.sender = username;
		info = "";
		results = new ArrayList<Object>();
	}
	
	
	public void setLoginInfo(LoginInfo login_info) {
		this.login_info = login_info;
	}
	
	public LoginInfo getLoginInfo() {
		return login_info;
	}
	
	public String[] getFiles() {
		return files;
	}
	
	public void addResults(Object o) {
		results.add(o);
	}
	
	public Object[] getResults() {
		Object[] r = new Object[results.size()];
		return results.toArray(r);
	}
	
	public String getSender() {
		return sender;
	}
	
	public String[] getArgs() {
		return args;
	}
	
	public int getMsg() {
		return msg;
	}
	
	public void setINFO(String info) {
		this.info = info;
	}
	
	public String getINFO() {
		return info;
	}
	
	
	@Override
	public String toString() {
		StringBuilder bob = new StringBuilder();
		bob.append("COMMAND: "+Message.getDescription(msg));
		bob.append("\nARGS: ");
		for (int i = 0; i < args.length; i++) {
			bob.append(args[i]);
			if(i != args.length-1) {
				bob.append(" | ");
			}
		}
		return bob.toString();
	}
	
}
