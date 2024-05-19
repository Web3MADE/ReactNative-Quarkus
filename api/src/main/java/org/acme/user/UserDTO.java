package org.acme.user;

import java.util.Set;
import java.util.stream.Collectors;
import org.acme.video.VideoDTO;

/** @dev UserDTO to be returned from API since it contains VideoDTOs within uploaded/liked videos */
public class UserDTO {
    public Long id;
    public String name;
    public String email;
    public Set<VideoDTO> uploadedVideos;
    public Set<VideoDTO> likedVideos;

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
}
