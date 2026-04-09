package photoUpload.organization.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class AuthResponse {
    
    private Long id;
    private String fullName;
    private String email;
    private String token; 

}
