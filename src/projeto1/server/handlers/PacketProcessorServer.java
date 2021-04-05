package projeto1.server.handlers;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import projeto1.sharedCore.Message;
import projeto1.sharedCore.MessagePacket;

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
			return processHistory(packet,ois,oos);
		case Message.COLLECT:
			return processCollect(packet,ois,oos);
		case Message.TRY_SEND_MSG:
			return processMessage(packet,ois,oos);
		case Message.GROUP_INFO:
			return processGroupInfo(packet);
		case Message.TRY_REMOVE_USER:
			return processRemoveUser(packet,ois,oos);
		case Message.TRY_ADD_USER:
			return processAddUser(packet,ois,oos);
		case Message.NEW_GROUP:
			return processNewGroup(packet,ois,oos);
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
		MessagePacket error = new MessagePacket(Message.FAIL,null,"server",null);
		error.setINFO("ERROR: Can not perfom that action");
		return error;
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
	private MessagePacket processNewGroup(MessagePacket packet, ObjectInputStream ois, ObjectOutputStream oos) {
		return groupHandler.createGroup(packet,ois,oos);
	}

	private MessagePacket processAddUser(MessagePacket packet, ObjectInputStream ois, ObjectOutputStream oos) {
		return groupHandler.addMember(packet,ois,oos);

	}

	private MessagePacket processRemoveUser(MessagePacket packet, ObjectInputStream ois, ObjectOutputStream oos) {
		return groupHandler.removeMember(packet,ois,oos);

	}

	private MessagePacket processGroupInfo(MessagePacket packet) {
		return groupHandler.groupInfo(packet);

	}

	private MessagePacket processMessage(MessagePacket packet, ObjectInputStream ois, ObjectOutputStream oos) {
		return groupHandler.sendMessage(packet,ois,oos);
	}

	private MessagePacket processCollect(MessagePacket packet, ObjectInputStream ois, ObjectOutputStream oos) {
		return groupHandler.collect(packet,ois,oos);
	}

	private MessagePacket processHistory(MessagePacket packet, ObjectInputStream ois, ObjectOutputStream oos) {
		return groupHandler.history(packet,ois,oos,ois,oos);

	}

	//////////////





}
