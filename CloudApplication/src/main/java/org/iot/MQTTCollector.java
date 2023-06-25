package org.iot;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.iot.dao.Dao;
import org.iot.models.Measurement;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MQTTCollector extends Thread implements MqttCallback{

    public void connectionLost(Throwable cause) {
        // TODO Auto-generated method stub
        System.err.println("Connection Lost because of [ " + cause.getCause() + " ]");
        try {
            Main.restartSubscriber();
        } finally{
            //System.exit(1);
        }
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("Message arrived!");
        String payload = new String(message.getPayload());
        Gson gson = new Gson();
        Measurement measure = gson.fromJson(payload, Measurement.class);

        LocalDateTime current = readTime();
        current = current.plusSeconds(Long.parseLong(measure.getTimestamp()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String strDate = formatter.format(current);
        measure.setTimestamp(strDate);


        System.out.println("Received "  + payload);
        System.out.println("Converted in " + measure);

        save(measure);

    }

    public void deliveryComplete(IMqttDeliveryToken token) {
        // TODO Auto-generated method stub

    }

    private LocalDateTime readTime() {
        return Main.startingTime;
    }
    public void save(Measurement measure){

        new Dao().writeMeasurement(measure);
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
