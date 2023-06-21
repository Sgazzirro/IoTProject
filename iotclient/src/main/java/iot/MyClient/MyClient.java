package iot.MyClient;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

public class MyClient{
	
	public static void main(String[] args) {
	CoapClient client = new CoapClient("coap://127.0.0.1/hello");
	CoapResponse response = client.get();
	System.out.println(response.getResponseText());
	
	}
	
}

