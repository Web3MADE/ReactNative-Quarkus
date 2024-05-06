package org.acme;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;

public class User {

    public static Multi<User> findAll(PgPool client) {
        return client
                .query("SELECT id, name, email, password FROM users")
                .execute()
                .onItem()
                .transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem()
                .transform(User::from);
    }

    public static Uni<User> findById(PgPool client, Long id) {
        return client
                .preparedQuery("SELECT id, name, email, password FROM users WHERE id = $1")
                .execute(Tuple.of(id))
                .onItem()
                .transform(user -> user.iterator().hasNext() ? User.from(user.iterator().next()) : null);
    }

    public static Uni<Long> save(PgPool client, String name, String email, String password) {
    return client
            .preparedQuery("INSERT INTO users (name, email, password) VALUES ($1, $2, $3) RETURNING id")
            .execute(Tuple.of(name, email, password))
            .onItem()
            .transform(pgRowSet -> pgRowSet.iterator().next().getLong("id"));
}

    private static User from(Row row) {
        return new User(row.getLong("id"), row.getString("name"), row.getString("email"), row.getString("password"));
    }
    private Long id;
    private String name;
    private String email;

    private String password;

    public User(Long id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
}
