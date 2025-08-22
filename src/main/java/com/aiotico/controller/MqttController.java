package com.aiotico.controller;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aiotico.dto.MqttPayload;
import com.aiotico.dto.VehicleDto;
import com.aiotico.dto.jpa.VehicleRepository;
import com.aiotico.entity.Vehicle;
import com.aiotico.gateway.MqttGateway;

@RestController
public class MqttController {

	private final VehicleRepository repo;
	private final GeometryFactory geometryFactory = new GeometryFactory();

	public MqttController(VehicleRepository repo) {
		this.repo = repo;
	}

	@Autowired
	MqttGateway mqtGateway;

	@PostMapping("/sendMessage")
	public ResponseEntity<?> publish(@RequestBody MqttPayload mqttPayload) {
		/*
		 * try { JsonObject convertObject = new Gson().fromJson(mqttMessage,
		 * JsonObject.class);
		 * mqtGateway.senToMqtt(convertObject.get("message").toString(),
		 * convertObject.get("topic").toString()); return ResponseEntity.ok("Success");
		 * } catch (Exception ex) { ex.printStackTrace(); return
		 * ResponseEntity.ok("fail"); }
		 */

		try {
			mqtGateway.senToMqtt(mqttPayload.getMessage(), mqttPayload.getTopic());
			return ResponseEntity.ok("Success");
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("fail");
		}
	}

	@PostMapping("/nearby")
	public List<Vehicle> getNearBy(@RequestBody VehicleDto vehicle) {
		Point point = geometryFactory.createPoint(new Coordinate(vehicle.getLongitude(), vehicle.getLatitude()));
		point.setSRID(4326);

		return repo.findNearbyVehicles(point, vehicle.getDistance());

	}

}
