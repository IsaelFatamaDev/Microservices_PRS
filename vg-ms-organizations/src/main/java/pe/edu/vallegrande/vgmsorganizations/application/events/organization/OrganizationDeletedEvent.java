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
public class OrganizationDeletedEvent {
    @Builder.Default
    private String eventType = "ORGANIZATION_DELETED";
    private String eventId;
    private LocalDateTime timestamp;
    private String organizationId;
    private String previousStatus;
    private String reason;
    private String deletedBy;
    private String correlationId;
}