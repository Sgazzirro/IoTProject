package org.iot;

import org.eclipse.californium.core.CoapServer;

public class COAPServer extends CoapServer implements Runnable{

    public void run(){
        System.out.println("[COAP Server]: I'm running");
        add(new CoapRegistration("actuators"));
        start();
    }

}
