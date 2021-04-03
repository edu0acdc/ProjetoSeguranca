package projeto1.server.handlers;

import java.io.File;

import projeto1.server.core.SystemSeiTchizServer;
import projeto1.server.exceptions.SystemLoadingException;

public class SystemLoader {
	
	private SystemLoader() {}

	
	public static void load(String keystore,String keystorePassword) throws SystemLoadingException{
		SystemSeiTchizServer.load(keystore,keystorePassword);
		File pubKeys = new File("PubKeys");
		if(!pubKeys.exists() || !pubKeys.isDirectory()) {
			if(!pubKeys.mkdir()) {
				throw new SystemLoadingException();
			}
			else {
				System.out.println("INFO: PubKeys folder created.");
			}
		}
		DatabaseLoader.loadDatabase(keystore,keystorePassword);	
	}
}
