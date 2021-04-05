package projeto1.client.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;

import projeto1.client.exceptions.ClientLoadingException;

public class ClientLoader {


	private ClientLoader() {}

	public static PrivateKey autoLoad(String client,String truststore) throws ClientLoadingException {
		try {
			checkInitialFolder();
			checkPersonalFolder(client);
			checkPhotosFolder(client);
			checkWallFolder(client);
			checkTrustStore(truststore);
			return checkAutoKeyStoreAndCert(client);
		}catch (Exception e) {
			e.printStackTrace();
			throw new ClientLoadingException();
		}
	}

	private static PrivateKey checkAutoKeyStoreAndCert(String client) throws ClientLoadingException {
		PrivateKey pk = SecurityChecker.getInstance().checkKeystore(client);
		Certificate c = SecurityChecker.getInstance().getCertificate(client);
		if(c == null) {
			System.out.println("ERROR: PLEASE CREATE KEYSTORE AND GENERATE KEY PAIR");
			throw new ClientLoadingException();
		}

		if(!(new File("PubKeys/"+client+".cer").exists())){
			try {
				FileOutputStream fos = new FileOutputStream("PubKeys/"+client+".cer");
				fos.write(c.getEncoded());
				fos.close();
			}catch (IOException | CertificateEncodingException e) {
				System.out.println("ERROR: ERROR WHILE GENERATING CERTIFICATE FROM KEYSTORE");
				throw new ClientLoadingException();
			}
		}
		return pk;

	}

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
	public static PrivateKey load(String client,String truststore,String keystore,String keystore_password) throws ClientLoadingException {
		try {
			checkInitialFolder();
			checkPersonalFolder(client);
			checkPhotosFolder(client);
			checkWallFolder(client);
			checkTrustStore(truststore);
			return checkKeyStoreAndCert(client,keystore,keystore_password);
		}catch (Exception e) {
			e.printStackTrace();
			throw new ClientLoadingException();
		}


	}

	private static PrivateKey checkKeyStoreAndCert(String client,String keystore, String keystore_password) throws ClientLoadingException {
		File user = new File("client/"+client);
		if(!user.exists()) {
			System.out.println("ERROR: client/"+client+" folder not found");
			throw new ClientLoadingException();
		}

		File cert = new File("PubKeys/"+client+".cer");
		if(!cert.exists()) {
			System.out.println("ERROR: "+client+".cer not found inside PubKeys folder");
			throw new ClientLoadingException();
		}
		File ksf = new File("client/"+client+"/"+keystore);
		if(!ksf.exists()) {
			System.out.println("ERROR: Keystore not found inside client/"+client+" folder");
			throw new ClientLoadingException();
		}
		
		return SecurityChecker.getInstance().getPrivateKey(client);


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

}
