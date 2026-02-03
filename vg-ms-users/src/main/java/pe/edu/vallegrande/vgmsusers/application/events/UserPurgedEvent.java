package pe.edu.vallegrande.vgmsusers.application.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPurgedEvent {

    @Builder.Default
    private String eventType = "USER_PURGED";

    private String eventId;
    private LocalDateTime timestamp;

    private String userId;
    private String organizationId;
    private String email;
    private String firstName;
    private String lastName;
    private String documentNumber;

    private String reason;

    private String purgedBy;
    private String correlationId;
}
