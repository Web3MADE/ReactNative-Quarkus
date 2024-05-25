package org.acme.services;

import java.util.List;
import java.util.stream.Collectors;
import org.acme.repositories.UserRepository;
import org.acme.user.User;
import org.acme.user.UserDTO;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

public class UserService {

    @Inject
    UserRepository userRepo;

    public Uni<List<UserDTO>> getAllUsers() {
        Uni<List<User>> users = userRepo.getAllUsers();
        return users
                .map(u -> u.stream().map(user -> new UserDTO(user)).collect(Collectors.toList()));
    }

    public Uni<UserDTO> getUserById(Long id) {
        Uni<User> user = userRepo.getUserById(id);
        return user.map(u -> new UserDTO(u));
    }

    public Uni<UserDTO> createUser(UserDTO userDTO) {
        return userRepo.createUser(userDTO);
    }

    public Uni<UserDTO> findByEmail(String email) {
        Uni<User> user = userRepo.findByEmail(email);
        return user.map(u -> new UserDTO(u));
    }
}
