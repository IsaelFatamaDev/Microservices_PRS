package pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects;

import java.math.BigDecimal;

/**
 * Value Object representing a material used in an incident resolution
 * Immutable by design
 */
public class MaterialUsed {
    
    private final String productId;
    private final Integer quantity;
    private final String unit;
    private final BigDecimal unitCost;

    public MaterialUsed(String productId, Integer quantity, String unit, BigDecimal unitCost) {
        this.productId = productId;
        this.quantity = quantity;
        this.unit = unit;
        this.unitCost = unitCost;
    }

    public String getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    /**
     * Calculates total cost of material used
     */
    public BigDecimal getTotalCost() {
        if (quantity == null || unitCost == null) {
            return BigDecimal.ZERO;
        }
        return unitCost.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialUsed that = (MaterialUsed) o;
        return java.util.Objects.equals(productId, that.productId) &&
               java.util.Objects.equals(quantity, that.quantity) &&
               java.util.Objects.equals(unit, that.unit) &&
               java.util.Objects.equals(unitCost, that.unitCost);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(productId, quantity, unit, unitCost);
    }

    @Override
    public String toString() {
        return "MaterialUsed{" +
                "productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                ", unitCost=" + unitCost +
                '}';
    }
}
