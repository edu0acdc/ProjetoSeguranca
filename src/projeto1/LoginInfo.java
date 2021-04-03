package projeto1;

import java.io.Serializable;

public class LoginInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 430284698612460557L;
	private byte[] nonce;
	private byte[] signature;
	private String certificate;
			
	public LoginInfo(byte[] nonce,byte[] signature,String certificate) {
		this.nonce = nonce.clone();
		this.signature = signature.clone();
		this.certificate = certificate;
	}
	
	public String getCertificate() {
		return certificate;
	}
	
	public byte[] getNonce() {
		return nonce;
	}
	
	public byte[] getSignature() {
		return signature;
	}
	
}
