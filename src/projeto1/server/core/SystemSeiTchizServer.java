package projeto1.server.core;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;

import javax.crypto.SecretKey;

import projeto1.server.database.DatabaseKeys;
import projeto1.server.exceptions.SystemLoadingException;
import projeto1.server.handlers.KeysLoader;

public class SystemSeiTchizServer {

	private static SystemSeiTchizServer singleton = null;
	private String keystore;
	private String keystorePassword;
	private DatabaseKeys keys = null;
	
	private SystemSeiTchizServer(String keystore,String keystorePassword) {
		this.keystore = keystore;
		this.keystorePassword = keystorePassword;
		keys = DatabaseKeys.getInstance(keystore, keystorePassword);
	}
	
	public static SystemSeiTchizServer getLoadedInstance() {
		return singleton;
	}

	public static SystemSeiTchizServer load(String keystore, String keystorePassword) throws SystemLoadingException {
		if(!KeysLoader.checkKeystoreAndCertifcate(keystore))
			throw new SystemLoadingException();
//		System.setProperty("javax.net.ssl.trustStore", "truststore.client");
		singleton = new SystemSeiTchizServer(keystore, keystorePassword);
		return singleton;
	}
	
	public String getKeystore() {
		return keystore;
	}
	
	public SecretKey getPrivateKey() {
		return (SecretKey) keys.getKey("secKey", "123456");
	}
}
