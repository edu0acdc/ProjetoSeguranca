package projeto1.server;

import java.io.IOException;
import projeto1.server.core.Server;
import projeto1.server.exceptions.SystemLoadingException;
import projeto1.server.handlers.SaveHandler;
import projeto1.server.handlers.SystemLoader;

public class RunServer{

	private static final String USAGE = "Usage -> SeiTchizServer [port] [keystore] [keystore-password]";

	public static void main(String[] args) {		
		try {	
			if(args.length != 4) {
				System.out.println(USAGE);
				return;
			}
			if(!args[0].contentEquals("SeiTchizServer")) {
				System.out.println(USAGE);
				return;
			}
			SystemLoader.load(args[2],args[3]);
			Server s = new Server(Integer.valueOf(args[1]));
			SaveHandler.save();
			s.start();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ERROR: FATAL ERROR WHILE STARTING SERVER");
		} catch (SystemLoadingException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
	}
}
