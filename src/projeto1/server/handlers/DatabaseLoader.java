package projeto1.server.handlers;

import java.io.File;

import projeto1.server.database.DatabaseClients;
import projeto1.server.database.DatabaseGroups;
import projeto1.server.database.DatabaseKeys;
import projeto1.server.database.DatabasePhotos;

public class DatabaseLoader {

	public static void loadDatabase(String keystore,String password) {
		File dir = new File("./server");
		if(!dir.isDirectory() || !dir.exists())
			dir.mkdir();
		
		DatabaseKeys.getInstance(keystore, password);
		DatabasePhotos.getInstance();
		DatabaseGroups.getInstance();
		DatabaseClients.getInstance();
	}
}
