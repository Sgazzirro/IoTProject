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
        String originalStatus[] = a.getStatus().split(",");

        CoapClient client = new CoapClient("coap://[" + a.getIPActuator() + "]:5683/fan");
        CoapResponse response = client.post(param + "=" + value, MediaTypeRegistry.TEXT_PLAIN);
        System.out.println(response.getResponseText());
        System.out.println(response.getCode());
        if(response.getCode() == CoAP.ResponseCode.CHANGED){
            // Cambia il json di a in maniera opportuna
            if(param.equals("temperature")){
                // Cambio il secondo numero della stringa status dentro a
                originalStatus[1] = String.valueOf(value);
            }
            else{
                originalStatus[0] = String.valueOf(value);
            }

            a.setStatus(originalStatus[0] + "," + originalStatus[1]);
            new DAO().writeActuator(a);
        }
    }

    public static void  incrDecrParam(Actuator a, String param, int value){
        String originalStatus[] = a.getStatus().split(",");

        CoapClient client = new CoapClient("coap://[" + a.getIPActuator() + "]:5683/fan");
        CoapResponse response = client.put(param + "=" + value, MediaTypeRegistry.TEXT_PLAIN);
        System.out.println(response.getResponseText());
        System.out.println(response.getCode());
        if(response.getCode() == CoAP.ResponseCode.CHANGED){
            // Cambia il json di a in maniera opportuna
            if(param.equals("temperature")){
                if(value == 1)
                // Cambio il secondo numero della stringa status dentro a
                    originalStatus[1] = String.valueOf(Integer.parseInt(originalStatus[1]) + 1);
                else
                    originalStatus[1] = String.valueOf(Integer.parseInt(originalStatus[1]) - 1);
            }
            else{
                if(value == 1) {
                    System.out.println("QQui ci entro");
                    // Cambio il secondo numero della stringa status dentro a
                    originalStatus[0] = String.valueOf(Integer.parseInt(originalStatus[0]) + 1);
                }
                else
                    originalStatus[0] = String.valueOf(Integer.parseInt(originalStatus[0]) - 1);
            }

            a.setStatus(originalStatus[0] + "," + originalStatus[1]);
            System.out.println(a);
            new DAO().writeActuator(a);
        }
    }


}
