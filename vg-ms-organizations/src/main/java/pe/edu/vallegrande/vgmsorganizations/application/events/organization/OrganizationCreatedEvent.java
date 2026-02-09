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
public class OrganizationCreatedEvent {
    @Builder.Default
    private String eventType = "ORGANIZATION_CREATED";
    private String eventId;
    private LocalDateTime timestamp;
    private String organizationId;
    private String organizationName;
    private String district;
    private String province;
    private String department;
    private String createdBy;
    private String correlationId;
}
