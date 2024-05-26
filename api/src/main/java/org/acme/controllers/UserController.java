package org.acme.controllers;

import java.util.List;
import org.acme.security.GenerateToken;
import org.acme.security.UserResponse;
import org.acme.services.UserService;
import org.acme.user.User;
import org.acme.user.UserDTO;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/api/users")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
// TODOs:
// add custom static queries to return User instead of generic PanacheEntity (findById returns a
// User class that doesn't need casting in-code)
public class UserController {

    @Inject
    UserService userService;

    @GET
    @PermitAll
    @WithSession
    // @RolesAllowed("Admin") remove for dev purposes
    public Uni<List<UserDTO>> getAllUsers() {
        return userService.getAllUsers();
    }

    @GET
    @Path("{id}")
    // @RolesAllowed({"User", "Admin"}) // only User or Admin role is authorized for this endpoint
    @PermitAll
    @WithSession
    public Uni<Response> get(@PathParam("id") Long id) {
        Uni<UserDTO> user = userService.getUserById(id);
        return user.onItem().transform(userDTO -> {
            if (userDTO == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.ok(userDTO).build();
            }
        });
    }

    @POST
    @PermitAll
    @WithTransaction
    public Uni<Response> createUser(UserDTO user) {
        System.out.println(
                "User: " + user.getName() + " " + user.getEmail() + " " + user.getPassword());
        Uni<User> createdUser = userService.createUser(user);

        return createdUser.onItem().transform(u -> {
            // Generate JWT Token
            String token = GenerateToken.generateJwtToken("https://example.com/issuer",
                    user.getEmail(), "User", "2001-07-13");
            UserResponse userResponse = new UserResponse(token, u.id);
            return Response.ok(userResponse).status(Response.Status.CREATED).build();
        });
    }

    @POST
    @Path("/login")
    @WithSession
    public Uni<Response> login(UserDTO user) {
        System.out.println("User: " + user.getEmail() + " " + user.getPassword());
        Uni<UserDTO> foundUser = userService.findByEmail(user.getEmail());

        return foundUser.onItem().transformToUni(userDTO -> {
            if (userDTO == null) {
                System.out.println("No user found for email: " + user.getEmail()); // Debug
                                                                                   // for no
                                                                                   // user
                return Uni.createFrom().item(Response.status(Response.Status.UNAUTHORIZED).build());
            }

            // Assuming passwords are stored in plain text for simplicity. In a real
            // application, passwords should be hashed and salted.
            if (!userDTO.getPassword().equals(user.getPassword())) {
                System.out.println("Incorrect password for email: " + user.getEmail()); // Debug
                                                                                        // for
                                                                                        // incorrect
                                                                                        // password
                return Uni.createFrom().item(Response.status(Response.Status.UNAUTHORIZED).build());
            }

            // Generate JWT Token
            String token = GenerateToken.generateJwtToken("https://example.com/issuer",
                    user.getEmail(), "User", "2001-07-13");
            UserResponse userResponse = new UserResponse(token, userDTO.getId());
            return Uni.createFrom().item(Response.ok(userResponse).build());
        });
    }
}
