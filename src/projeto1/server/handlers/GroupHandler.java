package projeto1.server.handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import projeto1.server.database.DatabaseClients;
import projeto1.server.database.DatabaseGroups;
import projeto1.sharedCore.GroupKey;
import projeto1.sharedCore.Message;
import projeto1.sharedCore.MessageGroup;
import projeto1.sharedCore.MessageGroupEncrypted;
import projeto1.sharedCore.MessagePacket;

public class GroupHandler {

	private DatabaseClients clients;
	private DatabaseGroups groups;
	private String sender;

	public GroupHandler() {
		clients = DatabaseClients.getInstance();
		groups = DatabaseGroups.getInstance();
		sender = "Server";
	}

	public MessagePacket addMember(MessagePacket pr, ObjectInputStream ois, ObjectOutputStream oos) {
		String[] args = pr.getArgs();
		String username = args[0];
		String groupID = args[1];
		MessagePacket ps = null;

		if(clients.checkAvailableUsername(username)) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("User doesn't exist.");
			return ps;
		}


		if(!groups.isOwner(pr.getSender(), groupID)) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("You are not the owner of this group.");
			return ps;
		}
		
		if(!groups.existsGroup(groupID)) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("Group not found.");
			return ps;
		}

		if(groups.isMember(username, groupID)) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("User already in group.");
			return ps;
		}
		
		
		groups.addMember(username, groupID);
		ps = new MessagePacket(Message.SUCCESS, new String[] {}, sender,new String[] {});
		ps.addResults(groups.getMembers(groupID));
		ps.addResults(groups.getCounterOfGroup(groupID));
		try {
			oos.writeObject(ps);
			
			MessagePacket ginfo = (MessagePacket) ois.readObject();
			if(ginfo.getMsg() != Message.ADD_USER) {
				ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
				ps.setINFO("Fatal error.");
				return ps;
			}
			groups.addMember(username, groupID);
			GroupKey[] keys = (GroupKey[]) ginfo.getResults()[0];
			groups.setNewKeys(groupID,keys);
			ps = new MessagePacket(Message.SUCCESS, new String[] {}, sender,new String[] {});
			ps.setINFO("User is now a member of this group.");
			return ps;
		} catch (IOException | ClassNotFoundException e) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("Fatal error.");
			return ps;
		}
		
		
	}

	public MessagePacket removeMember(MessagePacket pr, ObjectInputStream ois, ObjectOutputStream oos) {
		String[] args = pr.getArgs();
		String username = args[0];
		String groupID = args[1];
		MessagePacket ps = null;


		if(clients.checkAvailableUsername(username)) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("User doesn't exist.");
			return ps;
		}
		
		if(!groups.existsGroup(groupID)) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("Group not found.");
			return ps;
		}


		if(!groups.isOwner(pr.getSender(), groupID)) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("You are not the owner of this group.");
			return ps;
		}

		if(!groups.canRemoveMember(username, groupID)) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("User is not a normal member of this group.");
			return ps;
		}

		groups.removeMember(username, groupID);
		ps = new MessagePacket(Message.SUCCESS, new String[] {}, sender,new String[] {});
		ps.addResults(groups.getMembers(groupID));
		ps.addResults(groups.getCounterOfGroup(groupID));
		try {
			oos.writeObject(ps);
			
			MessagePacket ginfo = (MessagePacket) ois.readObject();
			if(ginfo.getMsg() != Message.REMOVE_USER) {
				ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
				ps.setINFO("Fatal error.");
				return ps;
			}
			GroupKey[] keys = (GroupKey[]) ginfo.getResults()[0];
			groups.setNewKeys(groupID,keys);
			ps = new MessagePacket(Message.SUCCESS, new String[] {}, sender,new String[] {});
			ps.setINFO("User is no longer member of this group.");
			return ps;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("Fatal error.");
			return ps;
		}
	}

	public MessagePacket createGroup(MessagePacket pr, ObjectInputStream ois, ObjectOutputStream oos) {
		String[] args = pr.getArgs();
		String groupID = args[0];
		MessagePacket ps = null;

		GroupKey gk = (GroupKey) pr.getResults()[0];
		if(!groups.createGroup(groupID,pr.getSender(),gk)) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("Group ID not available.");
			return ps;
		}

		ps = new MessagePacket(Message.SUCCESS, new String[] {}, sender,new String[] {});
		ps.setINFO("Group created.");
		return ps;
	} 

	public MessagePacket groupInfo(MessagePacket pr) {
		String[] args = pr.getArgs();
		MessagePacket ps = null;
		if(args.length > 0) {
			String groupID = args[0];	
			if(!groups.existsGroup(groupID)) {
				ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
				ps.setINFO("Group not found.");
				return ps;
			}


			if(!groups.isOwner(pr.getSender(), groupID)) {
				ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
				ps.setINFO("You are not the owner of this group.");
				return ps;
			}

			String[] aux = new String[] {groups.getGroupString(groupID)};

			ps = new MessagePacket(Message.SUCCESS, new String[] {}, sender, new String[] {});
			ps.addResults(aux);
			return ps;			
		}
		else {

			String[] own = groups.getGroupsOfOwner(pr.getSender());
			String[] belong = groups.getGroupsOfUser(pr.getSender());

			ps = new MessagePacket(Message.SUCCESS, new String[] {}, sender, new String[] {});
			ps.addResults(own);
			ps.addResults(belong);
			return ps;			

		}



	}

	public MessagePacket sendMessage(MessagePacket pr, ObjectInputStream ois, ObjectOutputStream oos) {
		String[] args = pr.getArgs();
		MessagePacket ps = null;
		String username = pr.getSender();
		String groupID = args[0];
		
		if(!groups.existsGroup(groupID)) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("Group not found.");
			return ps;
		}


		if(!groups.isMember(pr.getSender(), groupID)) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("You are not member of this group.");
			return ps;
		}


		ps = new MessagePacket(Message.SUCCESS, new String[] {}, sender, new String[] {});
		ps.setINFO("Counter sent.");
		GroupKey gk = groups.getLastKey(username,groupID);
		int gCounter = groups.getCounterOfGroup(groupID);
		if(gCounter != gk.getIdentficador()) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("You can not send message to this group.");
			return ps;
		}
		ps.addResults(gk);
		ps.addResults(gCounter);
		try {
			oos.writeObject(ps);
			MessagePacket msg = (MessagePacket) ois.readObject();
			if(msg.getMsg() != Message.MSG) {
				ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
				ps.setINFO("Fatal error.");
				return ps;
			}
			MessageGroup mg = (MessageGroup) msg.getResults()[0];
			groups.sendMessage(groupID,mg);						
			ps = new MessagePacket(Message.SUCCESS, new String[] {}, sender,new String[] {});
			ps.setINFO("Message sent.");
			return ps;
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("Fatal error.");
			return ps;
		}	


	}

	public MessagePacket collect(MessagePacket pr, ObjectInputStream ois, ObjectOutputStream oos) {
		String[] args = pr.getArgs();
		MessagePacket ps = null;

		String groupID = args[0];
		if(!groups.existsGroup(groupID)) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("Group not found.");
			return ps;
		}


		if(!groups.isMember(pr.getSender(), groupID)) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("You are not member of this group.");
			return ps;
		}

		MessageGroupEncrypted[] aux = groups.collect(pr.getSender(),groupID);
		if(aux.length > 0) {
			ps = new MessagePacket(Message.SUCCESS, new String[] {}, sender, new String[] {});
			ps.addResults(aux);
			return ps;		
		}
		else {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("No messages to collect.");
			return ps;
		}
	}
	
	public MessagePacket history(MessagePacket pr, ObjectInputStream ois, ObjectOutputStream oos, ObjectInputStream ois2, ObjectOutputStream oos2) {
		String[] args = pr.getArgs();
		MessagePacket ps = null;

		String groupID = args[0];
		if(!groups.existsGroup(groupID)) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("Group not found.");
			return ps;
		}


		if(!groups.isMember(pr.getSender(), groupID)) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("You are not member of this group.");
			return ps;
		}

		MessageGroupEncrypted[] aux = groups.history(pr.getSender(),groupID);
		if(aux.length > 0) {
			ps = new MessagePacket(Message.SUCCESS, new String[] {}, sender, new String[] {});
			ps.addResults(aux);
			return ps;		
		}
		else {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("No messages on your history.");
			return ps;
		}	
	}
	
	
}
