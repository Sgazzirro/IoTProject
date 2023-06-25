package org.iot;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.time.LocalDateTime;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static LocalDateTime startingTime;
    public static void main(String[] args) {
        startingTime = LocalDateTime.now();
        MQTTCollector newSub = new MQTTCollector();
        // Thread t = new Thread(new COAPServer());
        //t.start();
        newSub.start();
        while(true)
            ;
    }

    public static void restartSubscriber() {
        MQTTCollector newSub = new MQTTCollector();
        newSub.start();
    }

    public static void restartServerCoap(){
        Thread t = new Thread(new COAPServer());
        t.start();
    }
}