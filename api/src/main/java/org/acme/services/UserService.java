package org.acme.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.acme.repositories.UserRepository;
import org.acme.user.User;
import org.acme.user.UserDTO;
import org.acme.video.VideoDTO;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepo;

    public Uni<List<UserDTO>> getAllUsers() {
        return userRepo.getAllUsers().map(
                // this::mapUserToUserDTO is a method reference to mapUserToUserDTO
                users -> users.stream().map(this::mapUserToUserDTO).collect(Collectors.toList()));
    }

    public Uni<UserDTO> getUserById(Long id) {
        return userRepo.getUserById(id).map(this::mapUserToUserDTO);
    }

    public Uni<UserDTO> createUser(UserDTO userDTO) {
        return userRepo.createUser(userDTO);
    }

    public Uni<UserDTO> findByEmail(String email) {
        return userRepo.findByEmail(email).map(this::mapUserToUserDTO);
    }

    private UserDTO mapUserToUserDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.id);
        userDTO.setName(user.name);
        userDTO.setEmail(user.email);
        userDTO.setPassword(user.password);
        userDTO.setUploadedVideos(
                Optional.ofNullable(user.uploadedVideos).orElse(Collections.emptySet()).stream()
                        .map(VideoDTO::new).collect(Collectors.toSet()));
        userDTO.setLikedVideos(
                Optional.ofNullable(user.uploadedVideos).orElse(Collections.emptySet()).stream()
                        .map(VideoDTO::new).collect(Collectors.toSet()));

        return userDTO;
    }

    private User mapUserDTOtoUser(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User user = new User();
        user.id = userDTO.getId();
        user.name = userDTO.getName();
        user.email = userDTO.getEmail();
        user.password = userDTO.getPassword(); // TODO: hash password func

        return user;
    }
}
