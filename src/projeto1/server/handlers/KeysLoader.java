package projeto1.server.handlers;

import java.io.File;
public class KeysLoader {

	private KeysLoader() {}

	public static boolean checkKeystoreAndCertifcate(String keystore){
		File ks = new File(keystore);
		File cert = new File("serverCA.cer");
		return ks.exists() && cert.exists();
	}

	

}
