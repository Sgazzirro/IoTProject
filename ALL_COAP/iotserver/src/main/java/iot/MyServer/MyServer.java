package iot.MyServer;

import org.eclipse.californium.core.CoapServer;

public class MyServer extends CoapServer {

	public static void main(String[] args) {
		
		System.out.println("Prova di registrazione");
		MyServer server = new MyServer();
		server.add(new Registration("register"));
		server.start();
		
	}

}
