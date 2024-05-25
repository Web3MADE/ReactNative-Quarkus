package org.acme.user;

import java.util.Set;
import java.util.stream.Collectors;
import org.acme.video.VideoDTO;

/** @dev UserDTO to be returned from API since it contains VideoDTOs within uploaded/liked videos */
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Set<VideoDTO> uploadedVideos;
    private Set<VideoDTO> likedVideos;

    public UserDTO() {}

    public UserDTO(User user) {
        this.id = user.id;
        this.name = user.name;
        this.email = user.email;
        if (user.uploadedVideos != null) {
            // map each uploaded video to a VideoDTO
            this.uploadedVideos =
                    user.uploadedVideos.stream().map(VideoDTO::new).collect(Collectors.toSet());
        }
        if (user.likedVideos != null) {
            // map each liked video to a VideoDTO
            this.likedVideos =
                    user.likedVideos.stream().map(VideoDTO::new).collect(Collectors.toSet());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Set<VideoDTO> getUploadedVideos() {
        return uploadedVideos;
    }

    public void setUploadedVideos(Set<VideoDTO> uploadedVideos) {
        this.uploadedVideos = uploadedVideos;
    }

    public Set<VideoDTO> getLikedVideos() {
        return likedVideos;
    }

    public void setLikedVideos(Set<VideoDTO> likedVideos) {
        this.likedVideos = likedVideos;
    }
}
