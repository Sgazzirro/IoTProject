package iot.MyClient;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

public class MyClient{
	
	public static void main(String[] args) {
	
	CoapClient client = new CoapClient("coap://[fd00::202:2:2:2]:5683/fan");
	
	CoapResponse response = client.put("power=1&temperature=-1",MediaTypeRegistry.TEXT_PLAIN);
	System.out.println(response.getResponseText());
	System.out.println(response.getCode());
	
	}
	
}

