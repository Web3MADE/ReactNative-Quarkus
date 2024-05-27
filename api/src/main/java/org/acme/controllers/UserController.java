package org.acme.controllers;

import java.util.List;
import org.acme.services.JwtTokenService;
import org.acme.services.UserService;
import org.acme.user.UserDTO;
import org.acme.user.UserResponse;
import org.acme.utils.Constants;
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
public class UserController {

    @Inject
    UserService userService;

    @Inject
    JwtTokenService jwtTokenService;

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
        return userService.createUser(user).onItem().transform(u -> {
            System.out.println("User created: " + u.getToken() + " " + u.getUserId());
            return Response.ok(u).status(Response.Status.CREATED).build();
        }).onFailure().recoverWithItem(failure -> {
            System.out.println("Failed to create user: " + failure.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(failure.getMessage())
                    .build();
        });
    }

    @POST
    @Path("/login")
    @WithSession
    // TODO: clean up login code
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
            String token = jwtTokenService.generateJwtToken(Constants.JWT_ISSUER_URL,
                    user.getEmail(), Constants.Role.USER, Constants.JWT_BIRTHDATE);
            UserResponse userResponse =
                    new UserResponse(token, userDTO.getId(), userDTO.getName(), userDTO.getEmail());
            return Uni.createFrom().item(Response.ok(userResponse).build());
        });
    }
}
