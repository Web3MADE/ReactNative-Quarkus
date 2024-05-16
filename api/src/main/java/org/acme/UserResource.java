package org.acme;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import java.util.List;
import org.acme.security.GenerateToken;
import org.acme.security.UserResponse;
import org.hibernate.reactive.mutiny.Mutiny;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
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
import jakarta.ws.rs.core.Response.ResponseBuilder;


@Path("/api/users")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
// TODO: test out endpoints with new Reactive panache API
public class UserResource {

    @Inject
    Mutiny.SessionFactory sessionFactory;

    @Inject
    Vertx vertx;

    @GET
    @PermitAll
    // @RolesAllowed("Admin") remove for dev purposes
    public Uni<List<User>> getAll() {
        return User.listAll();
    }

    @GET
    @Path("{id}")
    @RolesAllowed({ "User", "Admin" }) // only User or Admin role is authorized for this endpoint
    public Uni<Response> get(@PathParam("id") Long id) {
        Uni<Response> responseUni = User.findById(id)
            .onItem().transform(user -> user != null ? Response.ok(user) : Response.status(Response.Status.NOT_FOUND))
            .onItem().transform(ResponseBuilder::build);
        final Uni<Response> response = responseUni;
        response.subscribe().with(result -> System.out.println(result));
        return response;
    }
    // TODO: Make this endpoint open to allow anyone to create a user
    @POST
    @PermitAll
    @WithTransaction
    // @Transactional // This annotation is required for transactional operations, such as database operations
    public Uni<Response> createUser(User user) {
        System.out.println("User: " + user.getName() + " " + user.getEmail() + " " + user.getPassword());
        if (user.id != null) {
            return Uni.createFrom().item(Response.status(BAD_REQUEST).build());
        }

        return user.persist()
            .onItem().transform(persistedUser -> {
                // Generate JWT Token
                String token = GenerateToken.generateJwtToken("https://example.com/issuer", user.getEmail(), "User", "2001-07-13");
                UserResponse userResponse = new UserResponse(token, user.id);
                return Response.ok(userResponse).status(Response.Status.CREATED).build();
            });
    }

    @POST
    @Path("/login")
    public Uni<Response> login(User user) {
        System.out.println("User: " + user.getEmail() + " " + user.getPassword());
         return User.find("email", user.getEmail()).firstResult()
            .onItem().transformToUni(foundUser -> {
                if (foundUser == null) {
                    System.out.println("No user found for email: " + user.getEmail());  // Debug for no user
                    return Uni.createFrom().item(Response.status(Response.Status.UNAUTHORIZED).build());
                }

                // Assuming passwords are stored in plain text for simplicity. In a real application, passwords should be hashed and salted.
                User existingUser = (User) foundUser;
                if (!existingUser.getPassword().equals(user.getPassword())) {
                    System.out.println("Incorrect password for email: " + user.getEmail());  // Debug for incorrect password
                    return Uni.createFrom().item(Response.status(Response.Status.UNAUTHORIZED).build());
                }

                // Generate JWT Token
                String token = GenerateToken.generateJwtToken("https://example.com/issuer", user.getEmail(), "User", "2001-07-13");
                UserResponse userResponse = new UserResponse(token, existingUser.id);
                return Uni.createFrom().item(Response.ok(userResponse).build());
            });
    }    
}
