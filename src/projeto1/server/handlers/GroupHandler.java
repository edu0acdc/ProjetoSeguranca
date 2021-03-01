package projeto1.server.handlers;

import projeto1.Message;
import projeto1.MessageGroup;
import projeto1.MessagePacket;
import projeto1.server.database.DatabaseClients;
import projeto1.server.database.DatabaseGroups;

public class GroupHandler {

	private DatabaseClients clients;
	private DatabaseGroups groups;
	private String sender;

	public GroupHandler() {
		clients = DatabaseClients.getInstance();
		groups = DatabaseGroups.getInstance();
		sender = "Server";
	}

	public MessagePacket addMember(MessagePacket pr) {
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

		if(!groups.addMember(username, groupID)) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("User already in group.");
			return ps;
		}

		ps = new MessagePacket(Message.SUCCESS, new String[] {}, sender,new String[] {});
		ps.setINFO("User is now a member of this group.");
		return ps;
	}

	public MessagePacket removeMember(MessagePacket pr) {
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

		if(!groups.removeMember(username, groupID)) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("User is not a normal member of this group.");
			return ps;
		}

		ps = new MessagePacket(Message.SUCCESS, new String[] {}, sender,new String[] {});
		ps.setINFO("User is no longer member of this group.");
		return ps;
	}

	public MessagePacket createGroup(MessagePacket pr) {
		String[] args = pr.getArgs();
		String groupID = args[0];
		MessagePacket ps = null;


		if(!groups.createGroup(groupID,pr.getSender())) {
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

	public MessagePacket sendMessage(MessagePacket pr) {
		String[] args = pr.getArgs();
		MessagePacket ps = null;

		String groupID = args[0];
		String msg = args[1];
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

		groups.sendMessage(pr.getSender(),msg,groupID);

		ps = new MessagePacket(Message.SUCCESS, new String[] {}, sender, new String[] {});
		ps.setINFO("Message sent.");
		return ps;			


	}

	public MessagePacket collect(MessagePacket pr) {
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

		MessageGroup[] aux = groups.collect(pr.getSender(),groupID);

		ps = new MessagePacket(Message.SUCCESS, new String[] {}, sender, new String[] {});
		ps.addResults(aux);
		return ps;			
	}
	
	public MessagePacket history(MessagePacket pr) {
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

		MessageGroup[] aux = groups.history(pr.getSender(),groupID);

		ps = new MessagePacket(Message.SUCCESS, new String[] {}, sender, new String[] {});
		ps.addResults(aux);
		return ps;		
	}
	
	
}
