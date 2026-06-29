package it.unifi.swam.parking.dtos;

import java.time.LocalDateTime;
import java.util.List;

public class ParkingPassDTO {
    private Long id;
    private LocalDateTime issueDate;
    private LocalDateTime expiryDate;
    private String zone;
    private Long ownerId;
    private List<String> licensePlates;

    public ParkingPassDTO() {
    }

    public ParkingPassDTO(Long id, LocalDateTime issueDate, LocalDateTime expiryDate, String zone, Long ownerId,
            List<String> licensePlates) {
        this.id = id;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.zone = zone;
        this.ownerId = ownerId;
        this.licensePlates = licensePlates;
    }

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

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public List<String> getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(List<String> licensePlates) {
        this.licensePlates = licensePlates;
    }
}
