package org.iot;

import org.eclipse.paho.client.mqttv3.MqttException;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        MQTTCollector newSub = new MQTTCollector();
        newSub.start();
    }

    public static void restartSubscriber() {
        MQTTCollector newSub = new MQTTCollector();
        newSub.start();
    }
}