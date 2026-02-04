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
public class UserPurgedEvent {
    private String userId;
    private Instant timestamp;

    public static final String ROUTING_KEY = "user.purged";
}
