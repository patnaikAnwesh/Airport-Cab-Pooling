package com.airportpooling.ridepooling.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Cab {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank(message = "driverName is required")
	private String driverName;
	@NotBlank(message = "licensePlate is required")
	private String licensePlate;
	@NotNull(message = "totalSeats is required")
	@Min(value = 1, message = "totalSeats must be at least 1")
	private Integer totalSeats;
	@NotNull(message = "luggageCapacity is required")
	@Min(value = 0, message = "luggageCapacity must be at least 0")
	private Integer luggageCapacity;

	@Enumerated(EnumType.STRING)
	private CabStatus status;

	@Version
	private Long version;

	public Cab() {
	}

	public Cab(Long id, String driverName, String licensePlate, Integer totalSeats, Integer luggageCapacity,
			CabStatus status, Long version) {
		this.id = id;
		this.driverName = driverName;
		this.licensePlate = licensePlate;
		this.totalSeats = totalSeats;
		this.luggageCapacity = luggageCapacity;
		this.status = status;
		this.version = version;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	public Integer getTotalSeats() {
		return totalSeats;
	}

	public void setTotalSeats(Integer totalSeats) {
		this.totalSeats = totalSeats;
	}

	public Integer getLuggageCapacity() {
		return luggageCapacity;
	}

	public void setLuggageCapacity(Integer luggageCapacity) {
		this.luggageCapacity = luggageCapacity;
	}

	public CabStatus getStatus() {
		return status;
	}

	public void setStatus(CabStatus status) {
		this.status = status;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
}
