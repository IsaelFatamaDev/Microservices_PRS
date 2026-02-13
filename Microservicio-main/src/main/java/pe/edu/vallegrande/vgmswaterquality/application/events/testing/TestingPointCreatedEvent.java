package pe.edu.vallegrande.vgmswaterquality.application.events.testing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestingPointCreatedEvent {
    @Builder.Default
    private String eventType="TESTING_CREATED";
    private String eventId;
    private LocalDateTime timestamp;
    private String organizationId;
    private String zoneId;
    private String updatedBy;
    private String createdBy;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    // Datos fijos
    private String pointName;
    private String pointType;
    private String location;
    private String latitude;
    private String longitude;
    private String recordStatus;

}
