package com.airportpooling.ridepooling.model;

import jakarta.persistence.*;

@Entity
public class Passenger {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String email;
	private Double detourTolerance;

	public Passenger() {
	}

	public Passenger(Long id, String name, String email, Double detourTolerance) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.detourTolerance = detourTolerance;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Double getDetourTolerance() {
		return detourTolerance;
	}

	public void setDetourTolerance(Double detourTolerance) {
		this.detourTolerance = detourTolerance;
	}
}
