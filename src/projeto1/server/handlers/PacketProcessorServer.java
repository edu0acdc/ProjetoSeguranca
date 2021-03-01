package projeto1.server.handlers;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import projeto1.Message;
import projeto1.MessagePacket;

public class PacketProcessorServer {


	private GroupHandler groupHandler;
	private FollowHandler followHandler;
	private PhotoHandler photoHandler;
	
	public PacketProcessorServer() {
		groupHandler = new GroupHandler();
		followHandler = new FollowHandler();
		photoHandler = new PhotoHandler();
	}

	public MessagePacket processPacket(MessagePacket packet, ObjectInputStream ois,ObjectOutputStream oos) {
		switch (packet.getMsg()) {
		//GROUP
		case Message.HISTORY:
			return processHistory(packet);
		case Message.COLLECT:
			return processCollect(packet);
		case Message.MSG:
			return processMessage(packet);
		case Message.GROUP_INFO:
			return processGroupInfo(packet);
		case Message.REMOVE_USER:
			return processRemoveUser(packet);
		case Message.ADD_USER:
			return processAddUser(packet);
		case Message.NEW_GROUP:
			return processNewGroup(packet);
			//

		case Message.LIKE:
			return processLike(packet);
		case Message.WALL:
			return processWall(packet,ois,oos);
		case Message.POST:
			return processPost(packet,ois,oos);
		case Message.VIEW_FOLLOWERS:
			return processViewFollowers(packet);
		case Message.UNFOLLOW:
			return processUnfollow(packet);
		case Message.FOLLOW:
			return processFollow(packet);
		default:
			break;
		}
		return null;
	}
	
	//FOLLOW

	private MessagePacket processFollow(MessagePacket packet) {
		return followHandler.follow(packet);
	}

	private MessagePacket processUnfollow(MessagePacket packet) {
		return followHandler.unfollow(packet);
	}

	private MessagePacket processViewFollowers(MessagePacket packet) {
		return followHandler.viewfollowers(packet);
	}
	
	/////
	
	
	///PHOTOS

	private MessagePacket processPost(MessagePacket packet, ObjectInputStream ois, ObjectOutputStream oos) {
		return photoHandler.post(packet,ois);
	}
	


	private MessagePacket processWall(MessagePacket packet, ObjectInputStream ois, ObjectOutputStream oos) {
		return photoHandler.wall(packet,ois,oos);
	}

	private MessagePacket processLike(MessagePacket packet) {
		return photoHandler.like(packet);
	}

	//GROUP
	private MessagePacket processNewGroup(MessagePacket packet) {
		return groupHandler.createGroup(packet);
	}

	private MessagePacket processAddUser(MessagePacket packet) {
		return groupHandler.addMember(packet);

	}

	private MessagePacket processRemoveUser(MessagePacket packet) {
		return groupHandler.removeMember(packet);

	}

	private MessagePacket processGroupInfo(MessagePacket packet) {
		return groupHandler.groupInfo(packet);

	}

	private MessagePacket processMessage(MessagePacket packet) {
		return groupHandler.sendMessage(packet);
	}

	private MessagePacket processCollect(MessagePacket packet) {
		return groupHandler.collect(packet);
	}

	private MessagePacket processHistory(MessagePacket packet) {
		return groupHandler.history(packet);

	}

	//////////////





}
