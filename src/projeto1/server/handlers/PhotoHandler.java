package projeto1.server.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


import projeto1.Message;
import projeto1.MessagePacket;
import projeto1.PartialFile;
import projeto1.PhotoInfo;
import projeto1.server.core.ClientInfo;
import projeto1.server.core.Photo;
import projeto1.server.database.DatabaseClients;
import projeto1.server.database.DatabasePhotos;

public class PhotoHandler {

	private DatabasePhotos photos;
	private DatabaseClients clients;
	private String sender;

	public PhotoHandler() {
		photos = DatabasePhotos.getInstance();
		clients = DatabaseClients.getInstance();
		sender = "server";
	}


	public MessagePacket like(MessagePacket pr) {
		MessagePacket ps = null;
		Long id = Long.valueOf(pr.getArgs()[0]);

		Photo p = photos.getPhoto(id);

		if(p == null) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("Photo id not found.");
			return ps;
		}

		ClientInfo sender_info = clients.getClient(pr.getSender());
		ClientInfo owner = p.getOwner();

		if(!sender_info.isFollowing(owner.getUsername())) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("You can not like this photo.");
			return ps;
		}

		if(!photos.like(sender_info.getUsername(),id)) {
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender,new String[] {});
			ps.setINFO("You already liked this photo.");
			return ps;
		}




		ps = new MessagePacket(Message.SUCCESS, new String[] {}, sender,new String[] {});
		ps.setINFO("Like done.");
		return ps;	
	}


	public MessagePacket post(MessagePacket packet, ObjectInputStream ois) {
		MessagePacket ps = null;
		File f = new File("server/"+packet.getSender()+"/"+packet.getArgs()[0]);
		try {
			PartialFile check = (PartialFile) ois.readObject();
			if(check.getMsg() == Message.NEW_FILE) {

				FileOutputStream fos = new FileOutputStream(f);

				while(true) {
					PartialFile pf = (PartialFile) ois.readObject();
					if(pf.getMsg() == Message.END_OF_FILE)
						break;
					fos.write(pf.getBytes(),0,pf.getLength());
				}
				fos.close();
				ps = new MessagePacket(Message.SUCCESS, new String[] {}, sender, new String[] {});
				ps.setINFO("Photo posted");
			}
			else
				throw new IOException();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender, new String[] {});
			ps.setINFO("Not possible to post photo");
			return ps;
		}
		ClientInfo c = clients.getClient(packet.getSender());
		photos.addPhoto(f, c);
		return ps;
	}


	public MessagePacket wall(MessagePacket packet, ObjectInputStream ois, ObjectOutputStream oos) {
		MessagePacket ps = null;
		String sender = packet.getSender();
		int n = Integer.parseInt(packet.getArgs()[0]);
		ClientInfo c = clients.getClient(sender);
		String[] fllw = c.getFollowing();
		List<Long> ids = new ArrayList<>();
		for (int i = 0; i < fllw.length; i++) {
			ids.addAll(clients.getClient(fllw[i]).getNPhotosID(n));
		}

		Photo[] phs = new Photo[ids.size()];
		try {
			for (int i = 0; i < phs.length; i++) {
				phs[i] = photos.getPhoto(ids.get(i));
				File f = phs[i].getFile();
				FileInputStream fis = new FileInputStream(f);
				PartialFile pf = new PartialFile(new byte[] {}, 0, Message.NEW_FILE, f.getName());
				oos.writeObject(pf);
				while(true) {
					byte[] bytes = new byte[1024];
					int bread = fis.read(bytes, 0, 1024);
					if(bread <= 0) {
						break;
					}
					oos.writeObject(new PartialFile(bytes, bread,Message.BYTES,f.getName()));
				}
				fis.close();
			}
			PartialFile pf = new PartialFile(new byte[] {}, 0, Message.NO_MORE_FILES, "");
			oos.writeObject(pf);
		}catch (IOException e) {
			e.printStackTrace();
			ps = new MessagePacket(Message.FAIL, new String[] {}, sender, new String[] {});
			ps.setINFO("Error while sending photos");
			return ps;
		}

		PhotoInfo[] pis = new PhotoInfo[ids.size()];
		for (int i = 0; i < pis.length; i++) {
			pis[i] = phs[i].toPhotoInfo();
		}

		ps = new MessagePacket(Message.SUCCESS, new String[] {}, sender, new String[] {});
		ps.addResults(pis);
		ps.setINFO("Wall sent");
		return ps;	
	}
}
