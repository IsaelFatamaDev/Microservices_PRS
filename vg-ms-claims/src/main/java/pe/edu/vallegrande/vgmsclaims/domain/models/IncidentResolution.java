package pe.edu.vallegrande.vgmsclaims.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.MaterialUsed;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.ResolutionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain entity representing an Incident Resolution.
 * Does not contain infrastructure annotations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentResolution {
    
    private String id;
    private String incidentId;
    private Instant resolutionDate;
    private ResolutionType resolutionType;
    private String actionsTaken;
    
    @Builder.Default
    private List<MaterialUsed> materialsUsed = new ArrayList<>();
    
    private Integer laborHours;
    private BigDecimal totalCost;
    private String resolvedByUserId;
    private Boolean qualityCheck;
    private Boolean followUpRequired;
    private String resolutionNotes;
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Calculates total cost of materials used
     */
    public BigDecimal calculateMaterialsCost() {
        if (materialsUsed == null || materialsUsed.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return materialsUsed.stream()
                .map(MaterialUsed::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Adds a used material to the resolution
     */
    public void addMaterial(MaterialUsed material) {
        if (materialsUsed == null) {
            materialsUsed = new ArrayList<>();
        }
        materialsUsed.add(material);
    }
}
