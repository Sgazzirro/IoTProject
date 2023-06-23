package org.iot;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.iot.models.Actuator;

public class ClientCOAP{

    // Qui vanno aggiunte le "convalidazioni sul database delle modifiche"
    public static void  enable(Actuator a){
        CoapClient client = new CoapClient("coap://[" + a.getIPActuator() + "]:5683/nitro");
        CoapResponse response = client.put("enable=on", MediaTypeRegistry.TEXT_PLAIN);
        System.out.println(response.getResponseText());
        System.out.println(response.getCode());

    }

    public static void  disable(Actuator a){
        CoapClient client = new CoapClient("coap://[" + a.getIPActuator() + "]:5683/nitro");
        CoapResponse response = client.delete();
        System.out.println(response.getResponseText());
        System.out.println(response.getCode());
    }

    public static void  changeParam(Actuator a, String param, int value){
        CoapClient client = new CoapClient("coap://[" + a.getIPActuator() + "]:5683/fan");
        CoapResponse response = client.post(param + "=" + value, MediaTypeRegistry.TEXT_PLAIN);
        System.out.println(response.getResponseText());
        System.out.println(response.getCode());
    }

    public static void  incrDecrParam(Actuator a, String param, int value){
        CoapClient client = new CoapClient("coap://[" + a.getIPActuator() + "]:5683/fan");
        CoapResponse response = client.put(param + "=" + value, MediaTypeRegistry.TEXT_PLAIN);
        System.out.println(response.getResponseText());
        System.out.println(response.getCode());
    }

    public static void main(String[] args) {
        CoapClient client = new CoapClient("coap://127.0.0.1/hello");
        CoapResponse response = client.get();
        System.out.println(response.getResponseText());

    }

}
