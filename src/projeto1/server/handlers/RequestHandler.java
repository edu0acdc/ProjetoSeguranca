package projeto1.server.handlers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import projeto1.LoginInfo;
import projeto1.MessagePacket;
import projeto1.server.core.ClientInfo;
import projeto1.server.database.DatabaseClients;
import projeto1.server.exceptions.InvalidCertificateException;

public class RequestHandler {

	private static RequestHandler singleton = null;
	private DatabaseClients database = DatabaseClients.getInstance();
	private PacketProcessorServer pps;
	
	private RequestHandler() {
		pps = new PacketProcessorServer();
	}
	
	public static RequestHandler getInstance() {
		if(singleton == null) {
			singleton = new RequestHandler();
		}
		return singleton;
	}

	public ClientInfo authenticate(String username,LoginInfo info) {
		PublicKey pk = database.getPublicKey(username);
		if(pk == null)
			return null;
		
		try {
			Signature s = Signature.getInstance("MD5withRSA");
			s.initVerify(pk);
			s.update(info.getNonce());
			if(s.verify(info.getSignature())) {
				return database.getClient(username);
			}
			return null;
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	public MessagePacket handlePacket(MessagePacket packet,ObjectInputStream ois,ObjectOutputStream oos) {
		return pps.processPacket(packet,ois,oos);
	}

	public boolean existsUser(String username) {
		return !database.checkAvailableUsername(username);
	}

	public ClientInfo register(String username, LoginInfo loginInfo) throws InvalidCertificateException  {
		try {
			FileInputStream fis = new FileInputStream(loginInfo.getCertificate());
			CertificateFactory cf = CertificateFactory.getInstance("X509");
			Certificate cert = cf.generateCertificate(fis);
			PublicKey publicKey = cert.getPublicKey();
			
			Signature s = Signature.getInstance("MD5withRSA");
			s.initVerify(publicKey);
			s.update(loginInfo.getNonce());
			if(s.verify(loginInfo.getSignature())) {
				return database.createClient(username, loginInfo.getCertificate());
			}			
			
		} catch (CertificateException  | InvalidKeyException | SignatureException e) {
			throw new InvalidCertificateException();
		} catch (FileNotFoundException | NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
}
