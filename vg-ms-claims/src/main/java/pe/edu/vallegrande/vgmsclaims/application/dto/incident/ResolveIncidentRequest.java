package pe.edu.vallegrande.vgmsclaims.application.dto.incident;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO to resolve an incident
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResolveIncidentRequest {
    
    @NotNull(message = "Resolution type is required")
    private String resolutionType;
    
    @NotBlank(message = "Actions taken are required")
    private String actionsTaken;
    
    private List<MaterialUsedRequest> materialsUsed;
    private Integer laborHours;
    private BigDecimal totalCost;
    private Boolean qualityCheck;
    private Boolean followUpRequired;
    private String resolutionNotes;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaterialUsedRequest {
        private String productId;
        private Integer quantity;
        private String unit;
        private BigDecimal unitCost;
    }
}
