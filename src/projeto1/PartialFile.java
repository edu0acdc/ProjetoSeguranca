package projeto1;

import java.io.Serializable;

public class PartialFile implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 265613236668628266L;
	private byte[] bytes;
	private int length;
	private int msg;
	private String filename;
	
	public PartialFile(byte[] bytes,int length, int msg,String filename) {
		this.bytes = bytes.clone();
		this.length = length;
		this.msg = msg;
		this.filename = filename;
	}
	
	public String getFilename() {
		return filename;
	}
	
	
	public int getMsg() {
		return msg;
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	
	public int getLength() {
		return length;
	}
	
	
}
