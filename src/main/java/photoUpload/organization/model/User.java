package photoUpload.organization.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(name = "full_name")
    private String fullName;
    private String email;
    private String password; // Oturum açmak için
    @Column(name = "phone_number")
    private String phoneNumber;

    //Soft Delete
    private boolean active=true;
    private LocalDateTime deletedAt;

}
