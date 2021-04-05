package projeto1.server.core;


import java.io.FileInputStream;
import java.security.KeyStore;

import javax.crypto.SecretKey;

import projeto1.server.exceptions.SystemLoadingException;
import projeto1.server.handlers.KeysLoader;

public class SystemSeiTchizServer {

	private static SystemSeiTchizServer singleton = null;
	private String keystore;
	
	private SystemSeiTchizServer(String keystore,String keystorePassword) {
		this.keystore = keystore;
	}
	
	public static SystemSeiTchizServer getLoadedInstance() {
		return singleton;
	}

	public static SystemSeiTchizServer load(String keystore, String keystorePassword) throws SystemLoadingException {
		if(!KeysLoader.checkKeystoreAndCertifcate(keystore))
			throw new SystemLoadingException();
		System.setProperty("javax.net.ssl.keyStore", keystore);
		System.setProperty("javax.net.ssl.keyStorePassword", keystorePassword);
		singleton = new SystemSeiTchizServer(keystore, keystorePassword);
		return singleton;
	}
	
	public String getKeystore() {
		return keystore;
	}
	
	public SecretKey getPrivateKey() {
		try {
			FileInputStream fis = new FileInputStream("encrypt.server");
			KeyStore ks = KeyStore.getInstance("JCEKS");
			ks.load(fis,"123456".toCharArray());
			return (SecretKey) ks.getKey("secKey", "123456".toCharArray());
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
