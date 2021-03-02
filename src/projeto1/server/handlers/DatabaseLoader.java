package projeto1.server.handlers;

import java.io.File;

import projeto1.server.database.DatabaseClients;
import projeto1.server.database.DatabaseGroups;
import projeto1.server.database.DatabasePhotos;

public class DatabaseLoader {

	public static void loadDatabase() {
		File dir = new File("./server");
		if(!dir.isDirectory() || !dir.exists())
			dir.mkdir();
		
		
		DatabasePhotos.getInstance();
		DatabaseGroups.getInstance();
		DatabaseClients.getInstance();
	}
}
