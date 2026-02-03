package pe.edu.vallegrande.vgmsusers.application.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdatedEvent {

    @Builder.Default
    private String eventType = "USER_UPDATED";
    private String eventId;
    private LocalDateTime timestamp;

    private String userId;
    private String organizationId;

    private Map<String, Object> changedFields;

    private String updatedBy;
    private String correlationId;
}
