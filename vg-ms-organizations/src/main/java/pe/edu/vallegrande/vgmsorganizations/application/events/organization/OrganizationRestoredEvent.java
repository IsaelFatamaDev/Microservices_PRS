package pe.edu.vallegrande.vgmsorganizations.application.events.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationRestoredEvent {
    @Builder.Default
    private String eventType = "ORGANIZATION_RESTORED";
    private String eventId;
    private LocalDateTime timestamp;
    private String organizationId;
    private String previousStatus;
    private String restoredBy;
    private String correlationId;
}