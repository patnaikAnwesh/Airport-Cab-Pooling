package com.airportpooling.ridepooling.dto;

import lombok.Data;

@Data
public class BookingRequest {
    private Long passengerId;
    private Double sourceLat;
    private Double sourceLng;
    private Double destLat;
    private Double destLng;
    public Long getPassengerId() {
		return passengerId;
	}
	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}
	public Double getSourceLat() {
		return sourceLat;
	}
	public void setSourceLat(Double sourceLat) {
		this.sourceLat = sourceLat;
	}
	public Double getSourceLng() {
		return sourceLng;
	}
	public void setSourceLng(Double sourceLng) {
		this.sourceLng = sourceLng;
	}
	public Double getDestLat() {
		return destLat;
	}
	public void setDestLat(Double destLat) {
		this.destLat = destLat;
	}
	public Double getDestLng() {
		return destLng;
	}
	public void setDestLng(Double destLng) {
		this.destLng = destLng;
	}
	public Integer getRequestedSeats() {
		return requestedSeats;
	}
	public void setRequestedSeats(Integer requestedSeats) {
		this.requestedSeats = requestedSeats;
	}
	public Integer getRequestedLuggage() {
		return requestedLuggage;
	}
	public void setRequestedLuggage(Integer requestedLuggage) {
		this.requestedLuggage = requestedLuggage;
	}
	private Integer requestedSeats;
    private Integer requestedLuggage;
}
