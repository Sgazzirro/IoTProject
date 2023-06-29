package org.iot;

import com.google.gson.Gson;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.iot.models.Actuator;
import org.iot.dao.Dao;

public class CoapRegistration extends CoapResource{
    public static int nextActuatorID = 0;
    private String actuatorIP;
    public CoapRegistration(String name) {
        super(name);

    }

    public void handleGET(CoapExchange exchange) {

        Response response = new Response(ResponseCode.CONTENT);

        response.setPayload("OK\n");

        exchange.respond(response);
    }

    public void handlePOST(CoapExchange exchange) {
        String parameters = exchange.getRequestText();
        
        System.out.println("Received a POST request\n" + parameters);

        System.out.println("FROM" + exchange.getSourceAddress().toString());
        actuatorIP = exchange.getSourceAddress().toString();
        actuatorIP = actuatorIP.substring(1);
        Response response = new Response(ResponseCode.CONTENT);
        response.setPayload("OKREG");
        exchange.respond(ResponseCode.CREATED);
	System.out.println("Trying to register actuator");
        registerActuator(parameters);
    }

    public void handleDELETE(CoapExchange exchange) {

        actuatorIP = exchange.getSourceAddress().toString();

        if(new Dao().deleteActuatorIP(actuatorIP.substring(1))){
            System.out.println("DELETE OK");
        }else{
            System.out.println("DELETE ERROR");
        }
        Response response = new Response(ResponseCode.CONTENT);
        
        response.setPayload("OKNREG");
        exchange.respond(ResponseCode.DELETED);

    }

    public void registerActuator(String parameters){
        Gson gson = new Gson();
        Actuator measure = gson.fromJson(parameters, Actuator.class);
        measure.setIDActuator(nextActuatorID++);
        measure.setIPActuator(actuatorIP);
        new Dao().writeActuator(measure);
    }



}
