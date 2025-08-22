package com.aiotico.dto.jpa;

import java.util.List;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.aiotico.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

	// Find vehicles within X meters of a given point
	@Query(value = "SELECT * FROM vehicles v WHERE ST_DWithin(v.location, :point, :distance)", nativeQuery = true)
	List<Vehicle> findNearbyVehicles(Point point, double distance);
}
