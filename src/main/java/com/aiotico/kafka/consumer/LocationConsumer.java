package com.aiotico.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class LocationConsumer {

	@KafkaListener(topics = "vts.location", groupId = "vts-group")
	public void consume(String payload) {
		// parse JSON, store, alert, etc.
		System.out.println("Received location: " + payload);
	}
}
