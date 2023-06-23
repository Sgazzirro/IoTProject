package iot.MyServer;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class CoAPResourceExample extends CoapResource {
	
	public CoAPResourceExample(String name) {
		super(name);
		setObservable(true);
		}
	
	public void handleGET(CoapExchange exchange) {
 		
 		Response response = new Response(ResponseCode.CONTENT);
 		
 		response.setPayload("Hello");
		
 		exchange.respond(response);
	}
 		
 		
 		
	/* public void handleGET(CoapExchange exchange) {
		Response response = new Response(ResponseCode.CONTENT);
		if(exchange.getRequestOptions().getAccept() == 
			MediaTypeRegistry.APPLICATION_XML){response.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_XML);
			response.setPayload("hello world from XML");
			}else if(exchange.getRequestOptions().getAccept() == MediaTypeRegistry.APPLICATION_JSON){
			response.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
			response.setPayload("hello world from JSON");
		}
		
		exchange.respond(response);
		}
	*/
		
		public void handlePOST(CoapExchange exchange) {
		String numero = exchange.getRequestText();
		exchange.respond(numero);
		System.out.println("post invoked in server;");
		exchange.respond(ResponseCode.CREATED);
		}
		

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
