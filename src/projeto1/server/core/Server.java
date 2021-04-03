package projeto1.server.core;

import java.io.IOException;
import java.net.Socket;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import projeto1.server.threads.*;

public class Server extends Thread{
	
	
	private int port;
	private SSLServerSocket ss;
	private ClientThreadManager manager;

	public Server(int port) throws IOException {
		this.port = port;
		ss = (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(port);
		manager = ClientThreadManager.getManager();
	}
	
	@Override
	public void run() {
		System.out.println("INFO: Server running at port "+port);
		while(true) {
			try {
				Socket new_socket = ss.accept();
				manager.addNewClient(new_socket);
			} catch (IOException e) {
				System.out.println("ERROR: Not possible to accept new socket connection");
			}
			
		}
		
		
	}
}
