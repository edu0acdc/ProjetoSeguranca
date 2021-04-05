package projeto1.server.handlers;

import projeto1.server.database.DatabaseClients;
import projeto1.sharedCore.Message;
import projeto1.sharedCore.MessagePacket;

public class FollowHandler {

	private DatabaseClients clients;
	private String sender;
	
	public FollowHandler() {
		clients = DatabaseClients.getInstance();
		sender = "Server";
	}

	public MessagePacket follow(MessagePacket pr) {
		String[] args = pr.getArgs();
		String userID = args[0];
		MessagePacket ps = null;

		if(clients.checkAvailableUsername(userID)) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("User doesn't exist.");
			return ps;
		}
		
		if(!clients.addFollower(pr.getSender(),userID)) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("Already following this user.");
			return ps;
		}
		
		ps = new MessagePacket(Message.SUCCESS, new String[] {}, sender,new String[] {});
		ps.setINFO("You are now following this user.");
		return ps;
	}

	public MessagePacket unfollow(MessagePacket pr) {
		String[] args = pr.getArgs();
		String userID = args[0];
		MessagePacket ps = null;

		if(clients.checkAvailableUsername(userID)) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("User doesn't exist.");
			return ps;
		}
		
		if(!clients.removeFollower(pr.getSender(),userID)) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("You are not following this user.");
			return ps;
		}
		
		ps = new MessagePacket(Message.SUCCESS, new String[] {}, sender,new String[] {});
		ps.setINFO("You are no longer following this user.");
		return ps;
	}

	public MessagePacket viewfollowers(MessagePacket pr) {
		MessagePacket ps = null;
		String[] followers = clients.getFollowers(pr.getSender());
		ps = new MessagePacket(Message.SUCCESS, new String[] {}, sender,new String[] {});
		ps.addResults(followers);
		return ps;		
	}

}
