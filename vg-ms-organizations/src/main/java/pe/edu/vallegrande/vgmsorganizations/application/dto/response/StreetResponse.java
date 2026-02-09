package pe.edu.vallegrande.vgmsorganizations.application.dto.street;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StreetResponse {
    private String id;
    private String organizationId;
    private String zoneId;
    private String streetType;
    private String streetTypeDisplayName;
    private String streetName;
    private String fullStreetName;
    private String recordStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}

