package projeto1;

import java.lang.reflect.Field;

public class Message {
	
	// LANGUAGE BETWEEN CLIENT-SERVER
	// 400-499 LOGIN
	public static final int LOGIN = 400;
	public static final int LOGIN_SUCCESS = 401;
	public static final int LOGIN_FAIL = 402;
	public static final int LOGIN_WRONG_PASSWORD = 403;
	public static final int NEED_USER_NAME = 404;
	public static final int USER_NAME = 405;
	public static final int NEED_CERT_AND_SIGN = 406;
	public static final int TRY_REGISTER = 407;
	public static final int NEED_SIGN = 408;
	public static final int TRY_SIGN = 409;
	
	// 500-599 WARNINGS
	public static final int SEND = 500;
	public static final int RECEIVE = 501;
	public static final int SUCCESS = 502;
	public static final int FAIL = 503;
	
	// COMMAND 100 - 199
	public static final int FOLLOW = 100;
	public static final int UNFOLLOW = 101;
	public static final int VIEW_FOLLOWERS = 102;
	public static final int POST = 103;
	public static final int WALL = 104;
	public static final int LIKE = 105;
	public static final int NEW_GROUP = 106;
	public static final int ADD_USER = 107;
	public static final int REMOVE_USER = 108;
	public static final int GROUP_INFO = 109;
	public static final int MSG = 110;
	public static final int COLLECT = 111;
	public static final int HISTORY = 112;
	
	
	//
	public static final int NEW_FILE = -1;
	public static final int BYTES = -2;
	public static final int END_OF_FILE = -3;
	public static final int NO_MORE_FILES = -4;
	
	
	private Message() {}

	public static String getDescription(int code) {
		for(Field f : Message.class.getFields()) {
			try {
				if(f.getInt(new Message()) == code) {
					return f.getName();
				}
			} catch (IllegalArgumentException e) {
				return null;
			} catch (IllegalAccessException e) {
				return null;
			}
		}
		return null;
		
	}

}
