package org.acme.user;

import java.util.Set;
import java.util.stream.Collectors;
import org.acme.video.VideoDTO;

public class UserDTO {
    public Long id;
    public String name;
    public String email;
    public Set<VideoDTO> uploadedVideos;

    public UserDTO(User user) {
        this.id = user.id;
        this.name = user.name;
        this.email = user.email;
        if (user.uploadedVideos != null) {
            this.uploadedVideos =
                    user.uploadedVideos.stream().map(VideoDTO::new).collect(Collectors.toSet());
        }
    }
}
