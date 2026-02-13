package pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Embedded document representing a material used in an incident resolution.
 * Stored as part of IncidentResolutionDocument.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialUsedEmbedded {
    
    private String productId;
    private Integer quantity;
    private String unit;
    private BigDecimal unitCost;
    
    /**
     * Calculates total cost of material
     */
    public BigDecimal getTotalCost() {
        if (quantity == null || unitCost == null) {
            return BigDecimal.ZERO;
        }
        return unitCost.multiply(BigDecimal.valueOf(quantity));
    }
}
