package it.unifi.swam.parking.rest;

import it.unifi.swam.parking.dtos.LoginRequest;
import it.unifi.swam.parking.dtos.UserDTO;
import it.unifi.swam.parking.mappers.UserMapper;
import it.unifi.swam.parking.model.User;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthenticationResource {

    @PersistenceContext(unitName = "parkingPU")
    private EntityManager em;

    @POST
    @Path("/login")
    public Response login(LoginRequest loginRequest) {
        System.out
                .println("Login attempt for username: " + (loginRequest != null ? loginRequest.getUsername() : "null"));
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :username AND u.password = :password", User.class);
            query.setParameter("username", loginRequest.getUsername());
            query.setParameter("password", loginRequest.getPassword());

            User user = query.getSingleResult();
            System.out.println("Login successful for user: " + user.getUsername());
            return Response.ok(UserMapper.toDTO(user)).build();
        } catch (jakarta.persistence.NoResultException e) {
            System.out.println("Login failed: User not found");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Invalid username or password\"}")
                    .build();
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"An error occurred during login\"}")
                    .build();
        }
    }

    @POST
    @Path("/register")
    @Transactional
    public Response register(UserDTO userDTO) {
        System.out.println("Registration attempt for username: " + (userDTO != null ? userDTO.getUsername() : "null"));
        try {
            User user = UserMapper.toEntity(userDTO);
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("CITIZEN");
            }
            em.persist(user);
            System.out.println("Registration successful for user: " + user.getUsername());
            return Response.status(Response.Status.CREATED).entity(UserMapper.toDTO(user)).build();
        } catch (Exception e) {
            System.out.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Could not register user: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}
