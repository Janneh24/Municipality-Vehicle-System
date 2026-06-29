package it.unifi.swam.parking.rest;

import it.unifi.swam.parking.dtos.ParkingPassDTO;
import it.unifi.swam.parking.mappers.ParkingPassMapper;
import it.unifi.swam.parking.model.ParkingPass;
import it.unifi.swam.parking.model.User;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequestScoped
@Path("/passes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ParkingPassResource {

    @PersistenceContext(unitName = "parkingPU")
    private EntityManager em;

    @GET
    @Transactional
    public Response getPasses(@QueryParam("userId") Long userId) {
        System.out.println("DEBUG: getPasses called for userId: " + userId);
        try {
            TypedQuery<ParkingPass> query;
            if (userId != null) {
                query = em.createQuery(
                        "SELECT DISTINCT p FROM ParkingPass p LEFT JOIN FETCH p.licensePlates WHERE p.owner.id = :userId",
                        ParkingPass.class);
                query.setParameter("userId", userId);
            } else {
                query = em.createQuery(
                        "SELECT DISTINCT p FROM ParkingPass p LEFT JOIN FETCH p.licensePlates",
                        ParkingPass.class);
            }

            List<ParkingPass> passEntities = query.getResultList();
            System.out.println("DEBUG: Found " + passEntities.size() + " pass entities in DB for userId " + userId);
            for (ParkingPass p : passEntities) {
                System.out.println("DEBUG: Pass ID: " + p.getId() + " ownerId: " + p.getOwner().getId());
            }

            List<ParkingPassDTO> passes = passEntities.stream()
                    .map(ParkingPassMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(passes).build();
        } catch (Exception e) {
            System.err.println("DEBUG ERROR in getPasses: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Internal server error fetching passes: " + e.getMessage() + "\"}").build();
        }
    }

    @POST
    @Transactional
    public Response purchasePass(ParkingPassDTO passDTO) {
        System.out.println("Purchase attempt for userId: " + (passDTO != null ? passDTO.getOwnerId() : "null"));
        try {
            User owner = em.find(User.class, passDTO.getOwnerId());
            if (owner == null) {
                System.out.println("Purchase failed: User not found");
                return Response.status(Response.Status.NOT_FOUND).entity("{\"error\": \"User not found\"}").build();
            }

            // check if user already has an active pass for this zone
            TypedQuery<Long> checkQuery = em.createQuery(
                    "SELECT COUNT(p) FROM ParkingPass p WHERE p.owner.id = :uid AND p.zone = :zone AND p.expiryDate > :now",
                    Long.class);
            checkQuery.setParameter("uid", owner.getId());
            checkQuery.setParameter("zone", passDTO.getZone());
            checkQuery.setParameter("now", LocalDateTime.now());

            if (checkQuery.getSingleResult() > 0) {
                System.out.println("Purchase failed: Active pass already exists for zone " + passDTO.getZone());
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Active pass already exists for this zone\"}").build();
            }

            ParkingPass pass = ParkingPassMapper.toEntity(passDTO, owner);
            if (pass.getIssueDate() == null)
                pass.setIssueDate(LocalDateTime.now());
            if (pass.getExpiryDate() == null)
                pass.setExpiryDate(LocalDateTime.now().plusMonths(1));

            // Enforce limit of 4 license plates
            if (pass.getLicensePlates().size() > 4) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"A parking pass can have at most 4 license plates\"}").build();
            }

            em.persist(pass);
            System.out.println("Purchase successful for pass ID: " + pass.getId());
            return Response.status(Response.Status.CREATED).entity(ParkingPassMapper.toDTO(pass)).build();
        } catch (Exception e) {
            System.err.println("Purchase error: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Could not purchase pass: " + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/check/{licensePlate}")
    public Response checkPass(@PathParam("licensePlate") String licensePlate) {
        // Find pass that contains this license plate and is not expired
        TypedQuery<ParkingPass> query = em.createQuery(
                "SELECT p FROM ParkingPass p JOIN p.licensePlates lp WHERE lp = :licensePlate AND p.expiryDate > :now",
                ParkingPass.class);
        query.setParameter("licensePlate", licensePlate);
        query.setParameter("now", LocalDateTime.now());

        List<ParkingPass> passes = query.getResultList();
        if (passes.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"status\": \"INVALID\"}").build();
        }
        return Response.ok("{\"status\": \"VALID\", \"expiryDate\": \"" + passes.get(0).getExpiryDate() + "\"}")
                .build();
    }

    @GET
    @Path("/count")
    public Response getActivePassCount() {
        Long count = em.createQuery("SELECT COUNT(p) FROM ParkingPass p WHERE p.expiryDate > :now", Long.class)
                .setParameter("now", LocalDateTime.now())
                .getSingleResult();
        return Response.ok(count).build();
    }
}
