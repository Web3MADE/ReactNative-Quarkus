package org.acme;
import org.acme.security.GenerateToken;
import org.acme.security.UserResponse;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
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
public class UserResource {

    @Inject
    PgPool client;

    @GET
    @RolesAllowed("Admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<User> getAll() {
        return User.findAll(client);
    }

    @GET
    @Path("{id}")
    @RolesAllowed({ "User", "Admin" }) // only User or Admin role is authorized for this endpoint
    public Uni<Response> get(@PathParam("id") Long id) {
        Uni<Response> responseUni = User.findById(client, id)
            .onItem().transform(user -> user != null ? Response.ok(user) : Response.status(Response.Status.NOT_FOUND))
            .onItem().transform(ResponseBuilder::build);
        final Uni<Response> response = responseUni;
        response.subscribe().with(result -> System.out.println(result));
        return response;
    }
    // TODO: Make this endpoint open to allow anyone to create a user
    @POST
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    // @Transactional // This annotation is required for transactional operations, such as database operations
    public Uni<Response> createUser(User user) {
        System.out.println("User: " + user.getName() + " " + user.getEmail() + " " + user.getPassword());
            return client.preparedQuery("INSERT INTO users (name, email, password) VALUES ($1, $2, $3) RETURNING id")
            .execute(Tuple.of(user.getName(), user.getEmail(), user.getPassword()))
            .onItem().transformToUni(idRowSet -> {
                if (idRowSet.rowCount() == 0) {
                    return Uni.createFrom().failure(new IllegalStateException("User was not created."));
                } else {
                   Long id = idRowSet.iterator().next().getLong("id");
                    String token = GenerateToken.generateJwtToken("https://example.com/issuer", user.getEmail(), "User", "2001-07-13");
                    UserResponse userResponse = new UserResponse(token, id);
                    return Uni.createFrom().item(
                        Response.ok(userResponse).build()
                    );
                }
            })
            .onFailure().recoverWithItem(t -> {
                t.printStackTrace();
                return Response.serverError().entity(t.getMessage()).build();
            });
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> login(User user) {
        System.out.println("User: " + user.getEmail() + " " + user.getPassword());
        return client.preparedQuery("SELECT * FROM users WHERE email = $1 AND password = $2")
            .execute(Tuple.of(user.getEmail(), user.getPassword()))
            .onItem().transform(pgRowSet -> {
                if (pgRowSet.size() == 0) {
                    System.out.println("No user found for email: " + user.getEmail());  // Debug for no user
                    return Response.status(Response.Status.UNAUTHORIZED).build();
                } else {
                    Long id = pgRowSet.iterator().next().getLong("id");
                    String token = GenerateToken.generateJwtToken("https://example.com/issuer", user.getEmail(), "User", "2001-07-13");
                    UserResponse userReponse = new UserResponse(token, id);
                    return Response.ok(userReponse).build();
                }
            })
            .onFailure().invoke(t -> {
            System.out.println("Error during login process: " + t.getMessage());  // Debug for any failure in the chain
            t.printStackTrace();
        });
    }

    @PostConstruct
    void config() {
        initdb();
    }
    // Initialize the database with some data
    private void initdb() {
        client.query("DROP TABLE IF EXISTS users").execute()
        .flatMap(u -> client.query("CREATE TABLE users (id SERIAL PRIMARY KEY, name TEXT NOT NULL, email TEXT NOT NULL, password TEXT NOT NULL)").execute())
        .flatMap(u -> client.query("INSERT INTO users (name, email, password) VALUES ('Bomberman', 'Bomberman@gmail.com', '123456')").execute())
        .onItem().invoke(pgRowSet -> {
            if (pgRowSet.rowCount() > 0) {
                System.out.println("User inserted successfully.");
            } else {
                System.out.println("User insertion failed.");
            }
        })
        .await().indefinitely();
        }

    
}
