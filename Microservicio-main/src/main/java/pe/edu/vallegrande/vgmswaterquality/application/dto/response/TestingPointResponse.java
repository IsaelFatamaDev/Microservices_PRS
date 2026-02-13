package pe.edu.vallegrande.vgmswaterquality.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestingPointResponse {
    private String id;
    private String organizationId;
    private String zoneId;
    private String pointName;
    private String pointType;
    private String location;
    private String latitude;
    private String longitude;
    private String recordStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
