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
public class UserDeletedEvent {

    @Builder.Default
    private String eventType = "USER_DELETED";
    private String eventId;
    private LocalDateTime timestamp;

    private String userId;
    private String organizationId;

    private String previousStatus;
    private String reason;

    private String deletedBy;
    private String correlationId;
}
