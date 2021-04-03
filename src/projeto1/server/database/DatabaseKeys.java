package projeto1.server.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class DatabaseKeys {
	
	private static DatabaseKeys singleton = null;
	private KeyStore ks;
	
	private DatabaseKeys(String keystore,String password) throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException {
		FileInputStream fis = new FileInputStream(keystore);
		ks = KeyStore.getInstance("JCEKS");
		ks.load(fis, password.toCharArray());
	}
	
	public static DatabaseKeys getInstance(String keystore,String password) {
		if(singleton == null) {
			try {
				singleton = new DatabaseKeys(keystore, password);
			}catch (Exception e) {
				singleton = null;
			}
		}
		return singleton;
	}
	
	
	
	public boolean hasAlias(String alias) {
		try {
			return ks.containsAlias(alias);
		}catch (Exception e) {
			return false;
			//Nunca entra aqui
		}
	}
	
	public boolean checkKey(String alias,String password,Key key) {
		try {
			Key k = ks.getKey(alias, password.toCharArray());
			return k.equals(key);
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
