package it.unifi.swam.parking.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "vehicle_types")
public class VehicleType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., CAR, MOTORBIKE

    public VehicleType() {
    }

    public VehicleType(String name) {
        this.name = name;
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
}
