package projeto1.server.handlers;

import projeto1.server.exceptions.SystemLoadingException;

public class SystemLoader {

	
	public static void load(String keystore) throws SystemLoadingException{
		if(!KeysLoader.checkKeystoreAndCertifcate(keystore))
			throw new SystemLoadingException();
		DatabaseLoader.loadDatabase();
	}
}
