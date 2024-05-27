package org.acme.user;

import java.util.Set;
import org.acme.video.Video;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User extends PanacheEntity {
    public String name;
    public String email;
    public String password;

    @OneToMany(mappedBy = "uploader", fetch = FetchType.EAGER)
    public Set<Video> uploadedVideos;
    @ManyToMany(fetch = FetchType.EAGER)
    public Set<Video> likedVideos;

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
