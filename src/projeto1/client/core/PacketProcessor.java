package projeto1.client.core;

import projeto1.Message;
import projeto1.MessageGroup;
import projeto1.MessagePacket;
import projeto1.PhotoInfo;

public class PacketProcessor {


	public PacketProcessor() {
		//no need to singleton
	}

	public void processPacket(int msg_sent,MessagePacket packet) {
		switch (msg_sent) {
		case Message.HISTORY:
			processHistory(packet);
			break;
		case Message.COLLECT:
			processCollect(packet);
			break;	
		case Message.MSG:
			processMessage(packet);
			break;
		case Message.GROUP_INFO:
			processGroupInfo(packet);
			break;
		case Message.REMOVE_USER:
			processRemoveUser(packet);
			break;
		case Message.ADD_USER:
			processAddUser(packet);
			break;
		case Message.NEW_GROUP:
			processNewGroup(packet);
			break;
		case Message.LIKE:
			processLike(packet);
			break;
		case Message.WALL:
			processWall(packet);
			break;
		case Message.POST:
			processPost(packet);
			break;
		case Message.VIEW_FOLLOWERS:
			processViewFollowers(packet);
			break;
		case Message.UNFOLLOW:
			processUnfollow(packet);
			break;
		case Message.FOLLOW:
			processFollow(packet);
			break;
		default:
			break;
		}
	}

	private void processFollow(MessagePacket packet) {
		if(packet.getMsg() == Message.SUCCESS) {
			System.out.print("SUCCESS: ");
			System.out.println(packet.getINFO());
		}
		else if(packet.getMsg() == Message.FAIL) {
			System.out.print("FAILED: ");
			System.out.println(packet.getINFO());
		}
		else {
			System.out.println(Message.getDescription(packet.getMsg()));
			System.out.println("ERROR: SOMETHING WENT WRONG");
		}

	}

	private void processUnfollow(MessagePacket packet) {
		if(packet.getMsg() == Message.SUCCESS) {
			System.out.print("SUCCESS: ");
			System.out.println(packet.getINFO());
		}
		else if(packet.getMsg() == Message.FAIL) {
			System.out.print("FAILED: ");
			System.out.println(packet.getINFO());
		}
		else {
			System.out.println("ERROR: SOMETHING WENT WRONG");
		}
	}

	private void processViewFollowers(MessagePacket packet) {
		if(packet.getMsg() == Message.SUCCESS) {
			String[] followers = (String[]) packet.getResults()[0];
			if(followers.length == 0) {
				System.out.println("You have no followers");
			}
			for (int i = 0; i < followers.length; i++) {
				System.out.println(followers[i]);
			}
		}
		else if(packet.getMsg() == Message.FAIL) {
			System.out.print("FAILED: ");
			System.out.println(packet.getINFO());
		}
		else {
			System.out.println("ERROR: SOMETHING WENT WRONG");
		}

	}

	private void processPost(MessagePacket packet) {
		if(packet.getMsg() == Message.SUCCESS) {
			System.out.print("SUCCESS: ");
			System.out.println(packet.getINFO());
		}
		else if(packet.getMsg() == Message.FAIL) {
			System.out.print("FAILED: ");
			System.out.println(packet.getINFO());
		}
		else {
			System.out.println("ERROR: SOMETHING WENT WRONG");
		}

	}

	private void processWall(MessagePacket packet) {
		if(packet.getMsg() == Message.SUCCESS) {
			PhotoInfo[] results = (PhotoInfo[]) packet.getResults()[0];
			if(results.length == 0) {
				System.out.println("No photos to see");
				return;
			}
			for (int i = 0; i < results.length; i++) {
				System.out.println(results[i]);
			}
		}
		else if(packet.getMsg() == Message.FAIL) {
			System.out.print("FAILED: ");
			System.out.println(packet.getINFO());
		}
		else {
			System.out.println("ERROR: SOMETHING WENT WRONG");
		}

	}

	private void processLike(MessagePacket packet) {
		if(packet.getMsg() == Message.SUCCESS) {
			System.out.print("SUCCESS: ");
			System.out.println(packet.getINFO());
		}
		else if(packet.getMsg() == Message.FAIL) {
			System.out.print("FAILED: ");
			System.out.println(packet.getINFO());
		}
		else {
			System.out.println("ERROR: SOMETHING WENT WRONG");
		}

	}

	private void processNewGroup(MessagePacket packet) {
		if(packet.getMsg() == Message.SUCCESS) {
			System.out.print("SUCCESS: ");
			System.out.println(packet.getINFO());
		}
		else if(packet.getMsg() == Message.FAIL) {
			System.out.print("FAILED: ");
			System.out.println(packet.getINFO());
		}
		else {
			System.out.println("ERROR: SOMETHING WENT WRONG");
		}

	}

	private void processAddUser(MessagePacket packet) {
		if(packet.getMsg() == Message.SUCCESS) {
			System.out.print("SUCCESS: ");
			System.out.println(packet.getINFO());
		}
		else if(packet.getMsg() == Message.FAIL) {
			System.out.print("FAILED: ");
			System.out.println(packet.getINFO());
		}
		else {
			System.out.println("ERROR: SOMETHING WENT WRONG");
		}
	}

	private void processRemoveUser(MessagePacket packet) {
		if(packet.getMsg() == Message.SUCCESS) {
			System.out.print("SUCCESS: ");
			System.out.println(packet.getINFO());
		}
		else if(packet.getMsg() == Message.FAIL) {
			System.out.print("FAILED: ");
			System.out.println(packet.getINFO());
		}
		else {
			System.out.println("ERROR: SOMETHING WENT WRONG");
		}

	}

	private void processGroupInfo(MessagePacket packet) {
		if(packet.getMsg() == Message.SUCCESS) {
			System.out.println("SUCCESS: ");
			Object[] results = packet.getResults();
			if(results.length == 2) {
				for (int i = 0; i < results.length; i++) {
					if(i == 0) {
						System.out.println("Own");
					}
					else {
						System.out.println("Member Of");
					}
					String[] r = (String[]) results[i];
					for (int j = 0; j < r.length; j++) {
						System.out.println(r[j]);
					}
					System.out.println();
				}
			}
			else {
				String[] r = (String[]) results[0];
				for (int i = 0; i < r.length; i++) {
					System.out.println(r[i]);
				}
			}
		}
		else if(packet.getMsg() == Message.FAIL) {
			System.out.print("FAILED: ");
			System.out.println(packet.getINFO());
		}
		else {
			System.out.println("ERROR: SOMETHING WENT WRONG");
		}

	}

	private void processMessage(MessagePacket packet) {
		if(packet.getMsg() == Message.SUCCESS) {
			System.out.print("SUCCESS: ");
			System.out.println(packet.getINFO());
		}
		else if(packet.getMsg() == Message.FAIL) {
			System.out.print("FAILED: ");
			System.out.println(packet.getINFO());
		}
		else {
			System.out.println("ERROR: SOMETHING WENT WRONG");
		}

	}

	private void processCollect(MessagePacket packet) {
		if(packet.getMsg() == Message.SUCCESS) {
			System.out.println("SUCCESS: ");
			MessageGroup[] msgs = (MessageGroup[]) packet.getResults()[0];
			if(msgs.length == 0) {
				System.out.println("No new messages to read.");
				return;
			}
			for (int i = 0; i < msgs.length; i++) {
				System.out.println(msgs[i]);
			}
		}
		else if(packet.getMsg() == Message.FAIL) {
			System.out.print("FAILED: ");
			System.out.println(packet.getINFO());
		}
		else {
			System.out.println("ERROR: SOMETHING WENT WRONG");
		}

	}

	private void processHistory(MessagePacket packet) {
		if(packet.getMsg() == Message.SUCCESS) {
			System.out.println("SUCCESS: ");
			MessageGroup[] msgs = (MessageGroup[]) packet.getResults()[0];
			for (int i = 0; i < msgs.length; i++) {
				System.out.println(msgs[i]);
			}
		}
		else if(packet.getMsg() == Message.FAIL) {
			System.out.print("FAILED: ");
			System.out.println(packet.getINFO());
		}
		else {
			System.out.println("ERROR: SOMETHING WENT WRONG");
		}

	}





}
