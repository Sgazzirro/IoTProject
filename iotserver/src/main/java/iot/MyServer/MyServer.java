package iot.MyServer;

import org.eclipse.californium.core.CoapServer;

public class MyServer extends CoapServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Prova");
		MyServer server = new MyServer();
		server.add(new CoAPResourceExample("hello"));
		server.start();
		
		
	}

}
