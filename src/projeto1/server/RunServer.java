package projeto1.server;

import java.io.IOException;

import projeto1.server.core.Server;
import projeto1.server.handlers.DatabaseLoader;

public class RunServer extends Thread{

	
	public static void main(String[] args) {
		DatabaseLoader.loadDatabase();
		try {	
			Server s = new Server(12345);
			s.start();
		} catch (IOException e) {
			System.out.println("ERROR: FATAL ERROR WHILE STARTING SERVER");
		}
		
		
		
		
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
	}
}
