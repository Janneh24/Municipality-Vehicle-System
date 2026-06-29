package it.unifi.swam.parking.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "parking_zones")
public class ParkingZone implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // e.g., Z1, Z2

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    public ParkingZone() {
    }

    public ParkingZone(String code, District district) {
        this.code = code;
        this.district = district;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }
}
