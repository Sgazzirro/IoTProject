package iot.MyServer;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class Registration extends CoapResource {
	
	public Registration(String name) {
		super(name);
		
		}
	
		public void handleGET(CoapExchange exchange) {
	 		
	 		Response response = new Response(ResponseCode.CONTENT);
	 		
	 		response.setPayload("OK\n");
			
	 		exchange.respond(response);
		}
	 		
 		
 
		public void handlePOST(CoapExchange exchange) {
		String parameters = exchange.getRequestText();
		//exchange.respond(numero);
		System.out.println("RICEVUTOs\n" + parameters);

		System.out.println("MITTENTE" + exchange.getSourceAddress().toString());


		Response response = new Response(ResponseCode.CONTENT);
		// qua se è an	dato a buon fine tutto 
		response.setPayload("OK\n");
		exchange.respond(ResponseCode.CREATED);
		
		}
		

	public static void main(String[] args) {
		

	}

}
