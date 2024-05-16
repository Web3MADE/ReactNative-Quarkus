package org.acme.user;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
// TODO: remove setters/getters 
// Use active record model: User object contains methods forf persisting state to the database
// Note: the quarkus-panache-mock dep allows mocking the static methods of the User class for testing
public class User extends PanacheEntity {
    private String name;

    private String email;

    private String password;

    public User() {
        
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}
