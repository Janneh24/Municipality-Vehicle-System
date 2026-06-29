package it.unifi.swam.parking.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "violations")
public class Violation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String district; // D1, D2, D3, D4, D5

    @Column(nullable = false)
    private String vehicleType; // CAR, MOTORBIKE

    @Column(nullable = false)
    private String engineType; // ELECTRIC, HYBRID, ICE

    @Column(nullable = false)
    private String licensePlate;

    public Violation() {
    }

    public Violation(LocalDateTime timestamp, String district, String vehicleType, String engineType,
            String licensePlate) {
        this.timestamp = timestamp;
        this.district = district;
        this.vehicleType = vehicleType;
        this.engineType = engineType;
        this.licensePlate = licensePlate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
}
