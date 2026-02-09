package pe.edu.vallegrande.vgmsorganizations.application.dto.response;

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
public class FareResponse {
    private String id;
    private String organizationId;
    private String fareType;
    private String fareTypeDisplayName;
    private Double amount;
    private String description;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private boolean currentlyValid;
    private String recordStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
