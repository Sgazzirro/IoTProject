package iot.MyClient;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

public class MyClient{
	
	public static void main(String[] args) {
	CoapClient client = new CoapClient("coap://[fd00::202:2:2:2]:5683/hello");
	System.out.println("Ce sto a provà");
	CoapResponse response = client.get();
	System.out.println(response.getResponseText());
	
	}
	
}

