package org.iot;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTCollector extends Thread implements MqttCallback{

    public void connectionLost(Throwable cause) {
        // TODO Auto-generated method stub
        System.err.println("Connection Lost because of [ " + cause.getMessage() + " ]");
        try {
            Main.restartSubscriber();
        } finally{
            System.exit(1);
        }
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // TODO Auto-generated method stub
        String payload = new String(message.getPayload());
        Gson gson = new Gson();
        Measurement measure = gson.fromJson(payload, Measurement.class);

        System.out.println("Received "  + payload);
        System.out.println("Converted in " + measure);

        save(measure);

    }

    public void deliveryComplete(IMqttDeliveryToken token) {
        // TODO Auto-generated method stub

    }

    public void save(Measurement measure){
        System.out.println("Saved!");
    }
/*
    public static void main(String[] args) {
        try {
            MQTTCollector mc = new MQTTCollector();
        } catch(MqttException me) {
            me.printStackTrace();
        }
    }
*/

    public void run(){

        try {
            sleep(3000);
            MqttClient mqttClient = new MqttClient("tcp://127.0.0.1:1883", "CLOUD_APPLICATION");
            mqttClient.setCallback(this);
            mqttClient.connect();
            mqttClient.subscribe("oxygen");
            System.out.println("Connected and Subscribed!");
        }
        catch(MqttException me){
            System.out.println("Error " + me.getMessage());
            Main.restartSubscriber();
            System.exit(1);
        }
        catch(Exception e){
            System.out.println("Error " + e.getMessage());
            Main.restartSubscriber();
            System.exit(1);
        }
        while(true)
            ;
    }

}
