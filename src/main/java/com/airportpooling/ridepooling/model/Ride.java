package com.airportpooling.ridepooling.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Ride {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Cab cab;

	@OneToMany(mappedBy = "ride", cascade = CascadeType.ALL)
	private List<Booking> bookings = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	private RideStatus status;

	private Double totalDistance;
	private Double totalDeviation;

	public Ride() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cab getCab() {
		return cab;
	}

	public void setCab(Cab cab) {
		this.cab = cab;
	}

	public List<Booking> getBookings() {
		return bookings;
	}

	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}

	public RideStatus getStatus() {
		return status;
	}

	public void setStatus(RideStatus status) {
		this.status = status;
	}

	public Double getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(Double totalDistance) {
		this.totalDistance = totalDistance;
	}

	public Double getTotalDeviation() {
		return totalDeviation;
	}

	public void setTotalDeviation(Double totalDeviation) {
		this.totalDeviation = totalDeviation;
	}
}
