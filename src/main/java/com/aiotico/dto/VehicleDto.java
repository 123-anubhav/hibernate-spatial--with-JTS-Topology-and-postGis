package com.aiotico.dto;

public class VehicleDto {

	private String vehicleId;
	private double latitude;
	private double longitude;
	private double distance;

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return "VehicleDto [vehicleId=" + vehicleId + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", distance=" + distance + "]";
	}

}
