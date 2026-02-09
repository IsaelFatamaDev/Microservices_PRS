package pe.edu.vallegrande.vgmsorganizations.application.events.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationUpdatedEvent {
    @Builder.Default
    private String eventType = "ORGANIZATION_UPDATED";
    private String eventId;
    private LocalDateTime timestamp;
    private String organizationId;
    private Map<String, Object> changedFields;
    private String updatedBy;
    private String correlationId;
}