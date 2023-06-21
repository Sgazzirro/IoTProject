package MyPublisher;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;



public class MyPublisher{
	
	public static void main(String[] args) {
	
		String topic = "alert";
		String content = "Message from Java Client";
		String broker = "tcp://127.0.0.1:1883";
		String clientId = "JavaApp1";
		
		try {
		MqttClient sampleClient = new MqttClient(broker, clientId);
		sampleClient.connect();
		MqttMessage message = new MqttMessage(content.getBytes());
		sampleClient.publish(topic, message);
		sampleClient.disconnect();
		} catch(MqttException me) {
		me.printStackTrace();
		}
}
}
