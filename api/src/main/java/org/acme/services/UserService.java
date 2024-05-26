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

// TODO: Do some BO refactoring
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

    public Uni<UserDTO> findByEmail(String email) {
        return userRepo.findByEmail(email).map(this::mapUserToUserDTO);
    }

    public List<VideoDTO> getLikedVideosByUser(UserDTO user) {
        if (user == null) {
            return null;
        }

        List<VideoDTO> likedVideos = user.getLikedVideos().stream().collect(Collectors.toList());
        return likedVideos;
    }

    public Uni<User> createUser(UserDTO userDTO) {
        if (userDTO.getId() != null) {
            return null;
        }
        return userRepo.createUser(userDTO);
    }

    public Uni<User> persistAndFlush(User user) {
        return userRepo.persistAndFlush(user);
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
        userDTO.setLikedVideos(Optional.ofNullable(user.likedVideos).orElse(Collections.emptySet())
                .stream().map(VideoDTO::new).collect(Collectors.toSet()));

        return userDTO;
    }
}
