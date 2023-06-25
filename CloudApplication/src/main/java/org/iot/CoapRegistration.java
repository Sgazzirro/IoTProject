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
        //exchange.respond(numero);
        System.out.println("RICEVUTOs\n" + parameters);

        System.out.println("MITTENTE" + exchange.getSourceAddress().toString());
        actuatorIP = exchange.getSourceAddress().toString();

        Response response = new Response(ResponseCode.CONTENT);
        // qua se è an	dato a buon fine tutto
        response.setPayload("OK");
        exchange.respond(ResponseCode.CREATED);

        registerActuator(parameters);
    }

    public void registerActuator(String parameters){
        Gson gson = new Gson();
        Actuator measure = gson.fromJson(parameters, Actuator.class);
        measure.setIDActuator(nextActuatorID++);
        measure.setIPActuator(actuatorIP);
        new Dao().writeActuator(measure);
    }



}
