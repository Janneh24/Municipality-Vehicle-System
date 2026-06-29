package it.unifi.swam.parking.rest;

import it.unifi.swam.parking.dtos.MasterDataDTO;
import it.unifi.swam.parking.dtos.ParkingZoneDTO;
import it.unifi.swam.parking.model.*;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@RequestScoped
@Path("/masterdata")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MasterDataResource {

    @PersistenceContext(unitName = "parkingPU")
    private EntityManager em;

    // Vehicle Types
    @GET
    @Path("/vehicle-types")
    public List<MasterDataDTO> getVehicleTypes() {
        return em.createQuery("SELECT vt FROM VehicleType vt", VehicleType.class)
                .getResultList().stream()
                .map(vt -> new MasterDataDTO(vt.getId(), vt.getName()))
                .collect(Collectors.toList());
    }

    @POST
    @Path("/vehicle-types")
    @Transactional
    public Response createVehicleType(MasterDataDTO dto) {
        VehicleType vt = new VehicleType(dto.getName());
        em.persist(vt);
        return Response.status(Response.Status.CREATED).entity(new MasterDataDTO(vt.getId(), vt.getName())).build();
    }

    @DELETE
    @Path("/vehicle-types/{id}")
    @Transactional
    public Response deleteVehicleType(@PathParam("id") Long id) {
        VehicleType vt = em.find(VehicleType.class, id);
        if (vt != null)
            em.remove(vt);
        return Response.noContent().build();
    }

    // Engine Types
    @GET
    @Path("/engine-types")
    public List<MasterDataDTO> getEngineTypes() {
        return em.createQuery("SELECT et FROM EngineType et", EngineType.class)
                .getResultList().stream()
                .map(et -> new MasterDataDTO(et.getId(), et.getName()))
                .collect(Collectors.toList());
    }

    @POST
    @Path("/engine-types")
    @Transactional
    public Response createEngineType(MasterDataDTO dto) {
        EngineType et = new EngineType(dto.getName());
        em.persist(et);
        return Response.status(Response.Status.CREATED).entity(new MasterDataDTO(et.getId(), et.getName())).build();
    }

    @DELETE
    @Path("/engine-types/{id}")
    @Transactional
    public Response deleteEngineType(@PathParam("id") Long id) {
        EngineType et = em.find(EngineType.class, id);
        if (et != null)
            em.remove(et);
        return Response.noContent().build();
    }

    // Districts
    @GET
    @Path("/districts")
    public List<MasterDataDTO> getDistricts() {
        return em.createQuery("SELECT d FROM District d", District.class)
                .getResultList().stream()
                .map(d -> new MasterDataDTO(d.getId(), d.getName(), d.getDescription()))
                .collect(Collectors.toList());
    }

    @POST
    @Path("/districts")
    @Transactional
    public Response createDistrict(MasterDataDTO dto) {
        District d = new District(dto.getName(), dto.getDescription());
        em.persist(d);
        return Response.status(Response.Status.CREATED)
                .entity(new MasterDataDTO(d.getId(), d.getName(), d.getDescription())).build();
    }

    @DELETE
    @Path("/districts/{id}")
    @Transactional
    public Response deleteDistrict(@PathParam("id") Long id) {
        District d = em.find(District.class, id);
        if (d != null)
            em.remove(d);
        return Response.noContent().build();
    }

    // Parking Zones
    @GET
    @Path("/zones")
    public List<ParkingZoneDTO> getZones() {
        return em.createQuery("SELECT z FROM ParkingZone z", ParkingZone.class)
                .getResultList().stream()
                .map(z -> {
                    ParkingZoneDTO dto = new ParkingZoneDTO();
                    dto.setId(z.getId());
                    dto.setCode(z.getCode());
                    if (z.getDistrict() != null) {
                        dto.setDistrictId(z.getDistrict().getId());
                        dto.setDistrictName(z.getDistrict().getName());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @POST
    @Path("/zones")
    @Transactional
    public Response createZone(ParkingZoneDTO dto) {
        District d = em.find(District.class, dto.getDistrictId());
        if (d == null)
            return Response.status(Response.Status.BAD_REQUEST).entity("District not found").build();

        ParkingZone z = new ParkingZone(dto.getCode(), d);
        em.persist(z);

        dto.setId(z.getId());
        dto.setDistrictName(d.getName());
        return Response.status(Response.Status.CREATED).entity(dto).build();
    }

    @DELETE
    @Path("/zones/{id}")
    @Transactional
    public Response deleteZone(@PathParam("id") Long id) {
        ParkingZone z = em.find(ParkingZone.class, id);
        if (z != null)
            em.remove(z);
        return Response.noContent().build();
    }
}
