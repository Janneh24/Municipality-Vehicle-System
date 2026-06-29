package it.unifi.swam.parking.rest;

import it.unifi.swam.parking.dtos.VehicleDTO;
import it.unifi.swam.parking.mappers.VehicleMapper;
import it.unifi.swam.parking.model.User;
import it.unifi.swam.parking.model.Vehicle;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@RequestScoped
@Path("/vehicles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VehicleResource {

    @PersistenceContext(unitName = "parkingPU")
    private EntityManager em;

    @GET
    public Response getVehicles(@QueryParam("userId") Long userId) {
        System.out.println("DEBUG: getVehicles for userId: " + userId);
        TypedQuery<Vehicle> query;
        if (userId != null) {
            query = em.createQuery("SELECT v FROM Vehicle v WHERE v.owner.id = :userId", Vehicle.class);
            query.setParameter("userId", userId);
        } else {
            query = em.createQuery("SELECT v FROM Vehicle v", Vehicle.class);
        }

        List<Vehicle> entities = query.getResultList();
        System.out.println("DEBUG: Found " + entities.size() + " vehicles");

        List<VehicleDTO> vehicles = entities.stream()
                .map(VehicleMapper::toDTO)
                .collect(Collectors.toList());
        return Response.ok(vehicles).build();
    }

    @POST
    @Transactional
    public Response registerVehicle(VehicleDTO vehicleDTO) {
        System.out.println(
                "DEBUG: Registering vehicle for ownerId: " + (vehicleDTO != null ? vehicleDTO.getOwnerId() : "null"));
        try {
            User owner = em.find(User.class, vehicleDTO.getOwnerId());
            if (owner == null) {
                System.out.println("DEBUG: Owner not found for ID: " + vehicleDTO.getOwnerId());
                return Response.status(Response.Status.NOT_FOUND).entity("{\"error\": \"User not found\"}").build();
            }
            Vehicle vehicle = VehicleMapper.toEntity(vehicleDTO, owner);
            em.persist(vehicle);
            System.out.println(
                    "DEBUG: Vehicle registered with ID: " + vehicle.getId() + " and owner ID: " + owner.getId());
            return Response.status(Response.Status.CREATED).entity(VehicleMapper.toDTO(vehicle)).build();
        } catch (Exception e) {
            System.out.println("DEBUG ERROR: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Could not register vehicle: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteVehicle(@PathParam("id") Long id) {
        Vehicle vehicle = em.find(Vehicle.class, id);
        if (vehicle != null) {
            em.remove(vehicle);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateVehicle(@PathParam("id") Long id, VehicleDTO vehicleDTO) {
        try {
            Vehicle vehicle = em.find(Vehicle.class, id);
            if (vehicle == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            vehicle.setLicensePlate(vehicleDTO.getLicensePlate());
            vehicle.setModel(vehicleDTO.getModel());
            vehicle.setColor(vehicleDTO.getColor());
            vehicle.setVehicleType(vehicleDTO.getVehicleType());
            vehicle.setEngineType(vehicleDTO.getEngineType());

            em.merge(vehicle);
            return Response.ok(VehicleMapper.toDTO(vehicle)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Could not update vehicle\"}")
                    .build();
        }
    }

    @GET
    @Path("/count")
    public Response getVehicleCount() {
        Long count = em.createQuery("SELECT COUNT(v) FROM Vehicle v", Long.class).getSingleResult();
        return Response.ok(count).build();
    }
}
