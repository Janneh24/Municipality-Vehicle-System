package it.unifi.swam.parking.rest;

import it.unifi.swam.parking.model.*;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@Singleton
@Startup
public class DataSeedService {

    @PersistenceContext(unitName = "parkingPU")
    private EntityManager em;

    @PostConstruct
    @Transactional
    public void seedData() {
        System.out.println("Checking if data seeding is required...");

        // Seed Districts
        if (em.createQuery("SELECT COUNT(d) FROM District d", Long.class).getSingleResult() == 0) {
            System.out.println("Seeding initial districts...");
            em.persist(new District("D1",
                    "Historic Center - city's core with main museums, institutions, and zones like San Jacopino, Piazza Puccini, Le Cascine"));
            em.persist(new District("D2",
                    "Campo di Marte - residential zones like Le Cure, Coverciano, Piazza Alberti, and sports areas near the Artemio Franchi Stadium"));
            em.persist(new District("D3",
                    "Gavinana-Galluzzo - southeast, with zones like Gavinana, Bandino, Sorgane, Nave a Rovezzano, and Galluzzo"));
            em.persist(new District("D4",
                    "Isolotto-Legnaia - southwest, with zones like Isolotto, Legnaia, Soffi ano, San Lorenzo a Greve, and Cintoia"));
            em.persist(new District("D5",
                    "Rifredi - northwest, with zones like Rifredi, Novoli, Brozzi, Peretola, Quaracchi, also with the Airport and the Careggi Hospital"));
        }

        // Seed Vehicle Types
        if (em.createQuery("SELECT COUNT(vt) FROM VehicleType vt", Long.class).getSingleResult() == 0) {
            System.out.println("Seeding initial vehicle types...");
            em.persist(new VehicleType("CAR"));
            em.persist(new VehicleType("MOTORBIKE"));
        }

        // Seed Engine Types
        if (em.createQuery("SELECT COUNT(et) FROM EngineType et", Long.class).getSingleResult() == 0) {
            System.out.println("Seeding initial engine types...");
            em.persist(new EngineType("ELECTRIC"));
            em.persist(new EngineType("HYBRID"));
            em.persist(new EngineType("ICE (Combustion)"));
        }

        // Seed Parking Zones
        if (em.createQuery("SELECT COUNT(z) FROM ParkingZone z", Long.class).getSingleResult() == 0) {
            System.out.println("Seeding initial parking zones...");
            List<District> districts = em.createQuery("SELECT d FROM District d", District.class).getResultList();
            for (District d : districts) {
                String prefix = d.getName().substring(0, 2); // e.g. "D1"
                em.persist(new ParkingZone(prefix + " - Center", d));
                em.persist(new ParkingZone(prefix + " - Residential", d));
            }
        }

        seedUsers();

        System.out.println("Data seeding check complete.");
    }

    private void seedUsers() {
        System.out.println("Ensuring initial users exist...");

        ensureUserExists("admin", "1234", "admin@swam.it", "System Admin", "D1", "ADMIN");
        ensureUserExists("citizen", "1234", "citizen@swam.it", "John Doe", "D2", "CITIZEN");
        ensureUserExists("police", "1234", "police@swam.it", "Officer Smith", "D1", "LAW_ENFORCEMENT");

        System.out.println("User seeding check complete.");
    }

    private void ensureUserExists(String username, String password, String email, String name, String district,
            String role) {
        List<User> users = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getResultList();

        if (users.isEmpty()) {
            System.out.println("Creating user: " + username);
            User user = new User(username, password, email, name, district, role);
            em.persist(user);
        } else {
            System.out.println("Updating user: " + username);
            User user = users.get(0);
            user.setPassword(password);
            user.setEmail(email);
            user.setFullName(name);
            user.setDistrict(district);
            user.setRole(role);
            em.merge(user);
        }
    }
}
