package com.airportpooling.ridepooling.model;

import jakarta.persistence.*;

@Entity
public class Booking {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Passenger passenger;

	@ManyToOne
	@JoinColumn(name = "ride_id")
	private Ride ride;

	private Double sourceLat;
	private Double sourceLng;
	private Double destLat;
	private Double destLng;

	private Integer requestedSeats;
	private Integer requestedLuggage;
	private Double price;

	@Enumerated(EnumType.STRING)
	private BookingStatus status;

	public Booking() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	public Ride getRide() {
		return ride;
	}

	public void setRide(Ride ride) {
		this.ride = ride;
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

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public void setStatus(BookingStatus status) {
		this.status = status;
	}
}
