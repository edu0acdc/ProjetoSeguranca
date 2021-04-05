package projeto1.server.handlers;

import projeto1.server.database.DatabaseClients;
import projeto1.server.database.DatabaseGroups;
import projeto1.server.database.DatabasePhotos;

public class SaveHandler {

	
	private SaveHandler() {
		
	}
	
	
	public static void save() {
		System.out.println("INFO: Saving database state");
		DatabaseClients.getInstance().save();
		DatabasePhotos.getInstance().save();
		DatabaseGroups.getInstance().save();
	}
}
