package org.acme.repositories;

import java.util.List;
import java.util.stream.Collectors;
import org.acme.user.User;
import org.acme.user.UserDTO;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    public Uni<List<User>> getAllUsers() {
        return User.listAll()
                .map(users -> users.stream().map(user -> (User) user).collect(Collectors.toList()));
    }

    public Uni<User> getUserById(Long id) {
        return User.findById(id).map(user -> ((User) user));
    }

    public Uni<User> createUser(UserDTO userDTO) {
        User user = new User();
        user.name = userDTO.getName();
        user.email = userDTO.getEmail();
        user.password = userDTO.getPassword();
        return user.persist().onItem().transform(u -> user);
    }

    public Uni<User> findByEmail(String email) {
        return User.find("email", email).firstResult();
    }
}
