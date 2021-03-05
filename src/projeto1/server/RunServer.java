package projeto1.server;

import java.io.IOException;

import projeto1.server.core.Server;
import projeto1.server.handlers.DatabaseLoader;

public class RunServer extends Thread{

	
	public static void main(String[] args) {
		DatabaseLoader.loadDatabase();
		try {	
			if(args.length != 2) {
				System.out.println("Usage -> SeiTchizServer [port]");
				return;
			}
			if(!args[0].contentEquals("SeiTchizServer")) {
				System.out.println("Usage -> SeiTchizServer [port]");
				return;
			}
			Server s = new Server(Integer.valueOf(args[1]));
			s.start();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ERROR: FATAL ERROR WHILE STARTING SERVER");
		}
		
		
		
		
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
	}
}
