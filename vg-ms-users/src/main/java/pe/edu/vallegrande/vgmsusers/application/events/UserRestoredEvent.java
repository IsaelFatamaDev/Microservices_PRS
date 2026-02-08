package pe.edu.vallegrande.vgmsusers.application.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRestoredEvent {

    @Builder.Default
    private String eventType = "USER_RESTORED";
    private String eventId;
    private Instant timestamp;

    private String userId;
    private String organizationId;

    private String previousStatus;

    private String restoredBy;
    private String correlationId;
}
