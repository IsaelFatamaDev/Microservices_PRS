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
public class UserDeletedEvent {
    private String userId;
    private String reason;
    private Instant timestamp;

    public static final String ROUTING_KEY = "user.deleted";
}
