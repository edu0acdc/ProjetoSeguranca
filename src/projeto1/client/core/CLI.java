package projeto1.client.core;

import java.util.Scanner;

import projeto1.Message;
import projeto1.MessagePacket;

public class CLI {


	private Scanner sc;
	private String username;
	
	public CLI(String username) {
		sc = new Scanner(System.in);
		this.username = username;
	}


	public String askPassword() {
		System.out.print("Password >");
		return sc.nextLine();
	}


	public MessagePacket menu() {
	
		System.out.print("> ");

		String cmd = sc.nextLine();
		
		if(cmd.length() == 0) return null;
		
		char cmd_c = cmd.charAt(0);

		switch (cmd_c) {
		case 'f':
			return follow(cmd);
		case 'u':
			return unFollow(cmd);
		case 'v':
			return viewFollowers(cmd);
		case 'p':
			return post(cmd);
		case 'w':
			return wall(cmd);
		case 'l':
			return like(cmd);
		case 'n':
			return newGroup(cmd);
		case 'a':
			return addUser(cmd);
		case 'r':
			return removeUser(cmd);
		case 'g':
			return groupInfo(cmd);
		case 'm':
			return message(cmd);
		case 'c':
			return collect(cmd);
		case 'h':
			return history(cmd);
		default:
			System.out.println("ERROR: COMMAND NOT FOUND");
			return null;
		}

	}


	private MessagePacket history(String cmd) {
		String[] aux = cmd.split(" ");
		if(aux.length != 2) {
			return null;
		}
		return new MessagePacket(Message.HISTORY, new String[]{aux[1]},username, new String[] {});
	}


	private MessagePacket collect(String cmd) {
		String[] aux = cmd.split(" ");
		if(aux.length != 2) {
			return null;
		}
		return new MessagePacket(Message.COLLECT, new String[]{aux[1]},username,new String[] {});
	}


	private MessagePacket message(String cmd) {
		String[] aux = cmd.split(" ");
		if(aux.length != 3) {
			return null;
		}
		return new MessagePacket(Message.MSG, new String[]{aux[1],aux[2]},username,new String[] {});
	}


	private MessagePacket groupInfo(String cmd) {
		String[] aux = cmd.split(" ");
		if(aux.length == 2) {
			return new MessagePacket(Message.GROUP_INFO, new String[]{aux[1]},username,new String[] {});
		}
		else {
			return new MessagePacket(Message.GROUP_INFO, new String[]{},username,new String[] {});
		}
		
	}


	private MessagePacket removeUser(String cmd) {
		String[] aux = cmd.split(" ");
		if(aux.length != 3) {
			return null;
		}
		return new MessagePacket(Message.REMOVE_USER, new String[]{aux[1],aux[2]},username,new String[] {});
	}


	private MessagePacket addUser(String cmd) {
		String[] aux = cmd.split(" ");
		if(aux.length != 3) {
			return null;
		}
		return new MessagePacket(Message.ADD_USER, new String[]{aux[1],aux[2]},username,new String[] {});
	}


	private MessagePacket newGroup(String cmd) {
		String[] aux = cmd.split(" ");
		if(aux.length != 2) {
			return null;
		}
		return new MessagePacket(Message.NEW_GROUP, new String[]{aux[1]},username,new String[] {});
	}


	private MessagePacket like(String cmd) {
		String[] aux = cmd.split(" ");
		if(aux.length != 2) {
			return null;
		}
		return new MessagePacket(Message.LIKE, new String[]{aux[1]},username,new String[] {});
	}


	private MessagePacket wall(String cmd) {
		String[] aux = cmd.split(" ");
		if(aux.length != 2) {
			return null;
		}
		try {
			Integer.parseInt(aux[1]);
		}catch (NumberFormatException e) {
			return null;
		}
		
		return new MessagePacket(Message.WALL, new String[]{aux[1]},username,new String[] {});
	}


	private MessagePacket post(String cmd) {
		String[] aux = cmd.split(" ");
		if(aux.length != 2) {
			return null;
		}
		return new MessagePacket(Message.POST, new String[]{aux[1]},username,new String[] {});
	}


	private MessagePacket viewFollowers(String cmd) {
		String[] aux = cmd.split(" ");
		if(aux.length != 1) {
			return null;
		}
		return new MessagePacket(Message.VIEW_FOLLOWERS, new String[]{},username,new String[] {});
	}


	private MessagePacket unFollow(String cmd) {
		String[] aux = cmd.split(" ");
		if(aux.length != 2) {
			return null;
		}
		return new MessagePacket(Message.UNFOLLOW, new String[]{aux[1]},username,new String[] {});
	}


	private MessagePacket follow(String cmd) {
		String[] aux = cmd.split(" ");
		if(aux.length != 2) {
			return null;
		}
		return new MessagePacket(Message.FOLLOW, new String[]{aux[1]},username,new String[] {});
	}


	public void printWelcome() {
		System.out.println("||||||||||||||||||||");
		System.out.println("||  SeiTchiz CLI  ||");
		System.out.println("||||||||||||||||||||");
	}


}
