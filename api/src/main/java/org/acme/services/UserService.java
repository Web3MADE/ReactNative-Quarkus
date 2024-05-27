package org.acme.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.acme.repositories.UserRepository;
import org.acme.user.User;
import org.acme.user.UserDTO;
import org.acme.user.UserResponse;
import org.acme.utils.Constants;
import org.acme.video.VideoDTO;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserService {

    @Inject
    JwtTokenService jwtTokenService;

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

    public Uni<UserResponse> createUser(UserDTO userDTO) {
        if (userDTO.getId() != null) {
            return Uni.createFrom().failure(new IllegalArgumentException("User already exists"));
        }
        Uni<User> createdUser = userRepo.createUser(userDTO);

        return createdUser.onItem().transform(user -> {
            // Generate JWT Token
            String token = jwtTokenService.generateJwtToken(Constants.JWT_ISSUER_URL,
                    user.getEmail(), Constants.Role.USER, Constants.JWT_BIRTHDATE);
            UserResponse userResponse = new UserResponse(token, user.id);
            return userResponse;
        });

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
