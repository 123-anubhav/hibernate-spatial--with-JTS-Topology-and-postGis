package com.aiotico.config;

import java.nio.charset.StandardCharsets;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttKafkaBridge {

	// inboud for subscribe outbound for publish in mqtt

	@Bean
	public MessageChannel mqttInputChannel() {
		return new DirectChannel();
	}

	// inboud for subscribe outbound for publish in mqtt

	@Bean
	public MqttPahoMessageDrivenChannelAdapter inbound() {
		/*
		 * MqttConnectOptions options = new MqttConnectOptions();
		 * options.setUserName("yourUser");
		 * options.setPassword("yourPass".toCharArray());
		 * 
		 * MqttPahoMessageDrivenChannelAdapter adapter = new
		 * MqttPahoMessageDrivenChannelAdapter("tcp://localhost:1883",
		 * "vts-mqtt-client", "vehicle/+/location"); adapter.setCompletionTimeout(5000);
		 * adapter.setConverter(new DefaultPahoMessageConverter()); adapter.setQos(1);
		 * adapter.setConnectOptions(options);
		 * adapter.setOutputChannel(mqttInputChannel()); return adapter;
		 */
		MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("serverIn",
				mqttClientFactory(), "#");

		adapter.setCompletionTimeout(5000);
		adapter.setConverter(new DefaultPahoMessageConverter());
		adapter.setQos(2);
		adapter.setOutputChannel(mqttInputChannel());
		return adapter;
	}

	// inboud for subscribe outbound for publish in mqtt

	@Bean
	public MqttPahoClientFactory mqttClientFactory() {
		DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
		MqttConnectOptions options = new MqttConnectOptions();

		options.setServerURIs(new String[] { "tcp://localhost:1883" });
		// options.setUserName("admin");
		// String pass = "12345678";
		// options.setPassword(pass.toCharArray());
		options.setCleanSession(true);

		factory.setConnectionOptions(options);

		return factory;
	}

	// inboud for subscribe outbound for publish in mqtt

	@Bean
	@ServiceActivator(inputChannel = "mqttInputChannel")
	public MessageHandler handler(KafkaTemplate<String, String> kafkaTemplate) {
		return message -> {
			String topic = "vts.location";
			/* ISSUE WITH KAFKA STREAM BCOS OF SERDE UNABLE TO CONVERT CLASS CAST B EXCEPTION THROW
			String payload = message.getPayload().toString();
			kafkaTemplate.send(topic, payload);
			
			*/
	        // ✅ Safely convert payload to UTF-8 string
	        Object payloadObj = message.getPayload();
	        String payload;

	        if (payloadObj instanceof byte[]) {
	            payload = new String((byte[]) payloadObj, StandardCharsets.UTF_8);
	        } else {
	            payload = payloadObj.toString(); // fallback
	        }
	        kafkaTemplate.send(topic, payload);			
		};
	}

	// inboud for subscribe outbound for publish in mqtt
	/**/
	@Bean
	public MessageChannel mqttOutboundChannel() {
		return new DirectChannel();
	}

	@Bean
	@ServiceActivator(inputChannel = "mqttOutboundChannel")
	public MessageHandler mqttOutbound() {
		// clientId is generated using a random number
		MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("serverOut", mqttClientFactory());
		messageHandler.setAsync(true);
		messageHandler.setDefaultTopic("#");
		messageHandler.setDefaultRetained(false);
		return messageHandler;
	}
	
	
	/*
	 * 
	 * 
	🔁 MQTT Directional Flow in Spring Integratio
	Flow Type -> Inbound
	Keyword -> Subscribe
	Purpose -> Receive messages from MQTT
	Spring Component -> MqttPahoMessageDrivenChannelAdapter


	Flow Type -> Outbound
	Keyword -> Publish
	Purpose -> Send messages to MQTT broker
	Spring Component ->  MqttPahoMessageHandler [package : org.springframework.integration.mqtt.outbound]

	 * */
	
	
	/*
	 *  Real-World Analogy (VTS Context)
- Inbound: Vehicle sends GPS → MQTT → Your app receives → Kafka → Dashboard
- Outbound: Your app sends CMS/PIS update → MQTT → Display board at station

	 * */
}
