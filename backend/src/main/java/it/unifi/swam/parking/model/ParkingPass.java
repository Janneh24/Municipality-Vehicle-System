package it.unifi.swam.parking.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parking_passes")
public class ParkingPass implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime issueDate;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private String zone;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @ElementCollection
    @CollectionTable(name = "parking_pass_plates", joinColumns = @JoinColumn(name = "pass_id"))
    @Column(name = "license_plate")
    private List<String> licensePlates = new ArrayList<>();

    public ParkingPass() {
    }

    public ParkingPass(LocalDateTime issueDate, LocalDateTime expiryDate, String zone, User owner) {
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.zone = zone;
        this.owner = owner;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<String> getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(List<String> licensePlates) {
        this.licensePlates = licensePlates;
    }
}
