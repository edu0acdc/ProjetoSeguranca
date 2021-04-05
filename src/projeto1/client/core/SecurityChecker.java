package projeto1.client.core;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import sun.security.x509.*;

public class SecurityChecker {

	private static final String defaultPassword = "123456";
	private static SecurityChecker singleton = null;

	private SecurityChecker() {}

	public static SecurityChecker getInstance() {
		if(singleton == null) {
			singleton = new SecurityChecker();
		}
		return singleton;
	}
	
	public PrivateKey checkKeystore(String username) {
		boolean newks = newKeyStore(username);
		if(newks) {
			PrivateKey pk = createCertificate(username);
			if(pk == null) {
				System.out.println("ERROR: FATAL ERROR CREATING CERTIFICATE");
				System.exit(1);
			}
			return pk;
		}
		else {
			return getPrivateKey(username);
		}
	}
	
	

	public PrivateKey getPrivateKey(String username) {
		try {
			FileInputStream keystore = new FileInputStream("client/"+username+"/keystore.client");
			KeyStore ks = KeyStore.getInstance("JCEKS");
			ks.load(keystore,defaultPassword.toCharArray());
			return (PrivateKey) ks.getKey("client",defaultPassword.toCharArray());
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private boolean newKeyStore(String username) {
		File ks = new File("client/"+username+"/keystore.client");
		if(!ks.exists()) {
			try {
				FileOutputStream fos = new FileOutputStream(ks);
				KeyStore kss = KeyStore.getInstance("JCEKS");
				kss.load(null,defaultPassword.toCharArray());
				kss.store(fos, defaultPassword.toCharArray());
				return true;
			} catch (NoSuchAlgorithmException | CertificateException | IOException | KeyStoreException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	private PrivateKey createCertificate(String username) {
		try {
			FileInputStream keystore = new FileInputStream("client/"+username+"/keystore.client");
			KeyStore ks = KeyStore.getInstance("JCEKS");
			ks.load(keystore,defaultPassword.toCharArray());
			keystore.close();
			KeyPair kp = KeyPairGenerator.getInstance("RSA").generateKeyPair();
			X509Certificate cert = generateCertificate("CN="+username+", OU=DI, O=FCUL,L=LISBON,ST=LISBON,C=PT",kp, 1, "SHA1withRSA");
			ks.setKeyEntry("client",kp.getPrivate(), defaultPassword.toCharArray(), new Certificate[] {cert});
			FileOutputStream fos= new FileOutputStream("client/"+username+"/keystore.client");
			ks.store(fos,defaultPassword.toCharArray());
			fos.close();
			return kp.getPrivate();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}


	public Certificate getCertificate(String username) {
		try {
			FileInputStream keystore = new FileInputStream("client/"+username+"/keystore.client");
			KeyStore ks = KeyStore.getInstance("JCEKS");
			ks.load(keystore,defaultPassword.toCharArray());
			return ks.getCertificate("client");
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/** 
	 * Create a self-signed X.509 Certificate
	 * @param dn the X.509 Distinguished Name, eg "CN=Test, L=London, C=GB"
	 * @param pair the KeyPair
	 * @param days how many days from now the Certificate is valid for
	 * @param algorithm the signing algorithm, eg "SHA1withRSA"
	 */ 
	private X509Certificate generateCertificate(String dn, KeyPair pair, int days, String algorithm)
	  throws GeneralSecurityException, IOException
	{
	  PrivateKey privkey = pair.getPrivate();
	  X509CertInfo info = new X509CertInfo();
	  Date from = new Date();
	  Date to = new Date(from.getTime() + days * 86400000l);
	  CertificateValidity interval = new CertificateValidity(from, to);
	  BigInteger sn = new BigInteger(64, new SecureRandom());
	  X500Name owner = new X500Name(dn);
	 
	  info.set(X509CertInfo.VALIDITY, interval);
	  info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(sn));
	  info.set(X509CertInfo.SUBJECT, owner);
	  info.set(X509CertInfo.ISSUER, owner);
	  info.set(X509CertInfo.KEY, new CertificateX509Key(pair.getPublic()));
	  info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
	  AlgorithmId algo = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);
	  info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algo));
	 
	  // Sign the cert to identify the algorithm that's used.
	  X509CertImpl cert = new X509CertImpl(info);
	  cert.sign(privkey, algorithm);
	 
	  // Update the algorith, and resign.
	  algo = (AlgorithmId)cert.get(X509CertImpl.SIG_ALG);
	  info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algo);
	  cert = new X509CertImpl(info);
	  cert.sign(privkey, algorithm);
	  return cert;
	}   


}
