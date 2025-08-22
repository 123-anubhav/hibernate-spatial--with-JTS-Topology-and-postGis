package com.aiotico.service;

import javax.transaction.Transactional;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import com.aiotico.dto.VehicleDto;
import com.aiotico.dto.jpa.VehicleRepository;
import com.aiotico.entity.Vehicle;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class VehicleService {
	
	 private final VehicleRepository repo;
	    private final ObjectMapper objectMapper = new ObjectMapper();
	    private final GeometryFactory geometryFactory = new GeometryFactory();

	    public VehicleService(VehicleRepository repo) {
	        this.repo = repo;
	    }

	    @Transactional
	    public void saveLocation(String json) {
	        try {
	            VehicleDto dto = objectMapper.readValue(json, VehicleDto.class);
	            Point point = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
	            point.setSRID(4326);

	            Vehicle vehicle = new Vehicle();
	            vehicle.setVehicleId(dto.getVehicleId());
	            vehicle.setLocation(point);
	            vehicle.setDistance(dto.getDistance()); // Add this line

	            repo.save(vehicle);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

}
