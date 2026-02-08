package pe.edu.vallegrande.vgmsauthentication.application.events.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRestoredEvent {
    private String userId;
    private Instant timestamp;

    public static final String ROUTING_KEY = "user.restored";
}
