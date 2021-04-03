package projeto1.client.core;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;

import projeto1.client.exceptions.ClientLoadingException;

public class ClientLoader {

	
	private ClientLoader() {}
	
	private static void checkInitialFolder() throws ClientLoadingException {
		System.out.println("INFO: Checking integrity of system");
		File folder_c = new File("./client");
		if(!folder_c.exists() || !folder_c.isDirectory()) {
			System.out.println("ERROR: Client folder not found, creating new one");
			if(!folder_c.mkdir()) {
				System.out.println("ERROR: Critical error, not possible to create client folder, exiting with status 1");
				throw new ClientLoadingException();
			}
			System.out.println("INFO: Client folder created");
		}
	}
	public static void load(String client,String truststore,String keystore,String keystore_password) throws ClientLoadingException {
		checkInitialFolder();
		checkPersonalFolder(client);
		checkPhotosFolder(client);
		checkWallFolder(client);
		checkTrustStore(truststore);
		checkKeyStoreAndCert(client,keystore,keystore_password);

	}

	private static void checkKeyStoreAndCert(String client,String keystore, String keystore_password) throws ClientLoadingException {
		
		File fks = new File("client/"+client+"/"+keystore);
		
		File cert = new File("./PubKeys/"+client+".cer");
		
		
		if(!fks.exists() || !cert.exists()) {
			throw new ClientLoadingException();
		}
	}

	private static void checkTrustStore(String truststore) throws ClientLoadingException {
		File ca = new File(truststore);
		if(!ca.exists()) { 
			throw new ClientLoadingException();
		}
		System.setProperty("javax.net.ssl.trustStore", truststore);
	}

	private static void checkWallFolder(String client) throws ClientLoadingException {
		File wall = new File("client/"+client+"/wall");
		if(!wall.exists() || !wall.isDirectory()) {
			System.out.println("ERROR: Wall folder not found, creating new one");
			if(!wall.mkdir()) {
				System.out.println("ERROR: Wall error, not possible to create client folder, exiting with status 1");
				throw new ClientLoadingException();
			}
			System.out.println("INFO: Wall folder created");
		}
	}

	private static void checkPhotosFolder(String client) throws ClientLoadingException {
		File photos = new File("client/"+client+"/photos");
		if(!photos.exists() || !photos.isDirectory()) {
			System.out.println("ERROR: Photos folder not found, creating new one");
			if(!photos.mkdir()) {
				System.out.println("ERROR: Critical error, not possible to create photos folder, exiting with status 1");
				throw new ClientLoadingException();
			}
			System.out.println("INFO: Photos folder created");
		}
	}

	private static void checkPersonalFolder(String client) throws ClientLoadingException {
		File folder_cp = new File("./client/"+client);
		if(!folder_cp.exists() || !folder_cp.isDirectory()) {
			System.out.println("ERROR: Personal Client folder not found, creating new one");
			if(!folder_cp.mkdir()) {
				System.out.println("ERROR: Critical error, not possible to create personal client folder, exiting with status 1");
				throw new ClientLoadingException();
			}
			System.out.println("INFO: Client folder created");
		}
	}

	public static PrivateKey getPrivateKey(String client,String keystore, String keystore_password) {
		try {
			FileInputStream kfile = new FileInputStream("client/"+client+"/"+keystore);
			KeyStore kstore = KeyStore.getInstance("JCEKS");
			kstore.load(kfile, keystore_password.toCharArray()); 
			return (PrivateKey) kstore.getKey(client, keystore_password.toCharArray());
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
}
