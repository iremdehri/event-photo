package photoUpload.organization.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="user")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String email;
    private String password; // Oturum açmak için
    private String phoneNumber;

    //Soft Delete
    private boolean active=true;
    private LocalDateTime deletedAt;

}
