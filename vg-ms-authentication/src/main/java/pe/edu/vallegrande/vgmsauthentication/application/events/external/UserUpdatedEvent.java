package pe.edu.vallegrande.vgmsauthentication.application.events.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatedEvent {

    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String organizationId;
    private Instant timestamp;

    public static final String ROUTING_KEY = "user.updated";
}
