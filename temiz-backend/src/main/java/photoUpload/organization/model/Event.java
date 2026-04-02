package photoUpload.organization.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(name="sub_title",nullable = true)
    private String subTitle;
    private String coverUri;
    @Column(unique = true,nullable = false)
    private String uuid;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate(){
        this.uuid= UUID.randomUUID().toString();
        this.createdAt=LocalDateTime.now();
    }

}
