package MySubscriber;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MySubscriber implements MqttCallback {
			public MySubscriber() throws MqttException {
			MqttClient mqttClient = new MqttClient("tcp://127.0.0.1:1883","JavaApp2");
			mqttClient.setCallback( this );
			mqttClient.connect();
			mqttClient.subscribe("alert");
			}

			public void connectionLost(Throwable cause) {
				// TODO Auto-generated method stub
				
			}

			public void messageArrived(String topic, MqttMessage message) throws Exception {
				// TODO Auto-generated method stub
				System.out.println(String.format("[%s] %s", topic, new String(message.getPayload())));

			}

			public void deliveryComplete(IMqttDeliveryToken token) {
				// TODO Auto-generated method stub
				
			}
			
			public static void main(String[] args) {
				try {
					MySubscriber mc = new MySubscriber();
				} catch(MqttException me) {
				me.printStackTrace();
				}
				}

			
			}
