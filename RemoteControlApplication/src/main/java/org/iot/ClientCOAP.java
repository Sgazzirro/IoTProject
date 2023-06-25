package org.iot;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.iot.models.Actuator;
import org.iot.dao.DAO;
public class ClientCOAP{

    // Qui vanno aggiunte le "convalidazioni sul database delle modifiche"
    public static void  enable(Actuator a){
        CoapClient client = new CoapClient("coap://[" + a.getIPActuator() + "]:5683/nitro");
        CoapResponse response = client.put("enable=on", MediaTypeRegistry.TEXT_PLAIN);
        System.out.println(response.getResponseText());
        System.out.println(response.getCode());
        if(response.getCode() == CoAP.ResponseCode.CHANGED){
            a.setStatus("ON");
            new DAO().writeActuator(a);
        }

    }

    public static void  disable(Actuator a){
        CoapClient client = new CoapClient("coap://[" + a.getIPActuator() + "]:5683/nitro");
        CoapResponse response = client.delete();
        System.out.println(response.getResponseText());
        System.out.println(response.getCode());
        if(response.getCode() == CoAP.ResponseCode.CHANGED){
            a.setStatus("OFF");
            new DAO().writeActuator(a);
        }
    }

    public static void  changeParam(Actuator a, String param, int value){
        CoapClient client = new CoapClient("coap://[" + a.getIPActuator() + "]:5683/fan");
        CoapResponse response = client.post(param + "=" + value, MediaTypeRegistry.TEXT_PLAIN);
        System.out.println(response.getResponseText());
        System.out.println(response.getCode());
        if(response.getCode() == CoAP.ResponseCode.CHANGED){
            a.setStatus(String.valueOf(value));
            new DAO().writeActuator(a);
        }
    }

    public static void  incrDecrParam(Actuator a, String param, int value){
        CoapClient client = new CoapClient("coap://[" + a.getIPActuator() + "]:5683/fan");
        CoapResponse response = client.put(param + "=" + value, MediaTypeRegistry.TEXT_PLAIN);
        System.out.println(response.getResponseText());
        System.out.println(response.getCode());
        if(response.getCode() == CoAP.ResponseCode.CHANGED){
            if(value == 1)
                a.setStatus(String.valueOf(Integer.parseInt(a.getStatus()) + 1));
            if(value == -1)
                a.setStatus(String.valueOf(Integer.parseInt(a.getStatus()) + 1));
            new DAO().writeActuator(a);
        }
    }


}
