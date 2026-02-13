package pe.edu.vallegrande.vgmscommercial.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.MovementCategory;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.MovementType;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PettyCashMovement {

    private String id;
    private String organizationId;
    private LocalDateTime createdAt;
    private String createdBy;

    private String pettyCashId;
    private LocalDateTime movementDate;
    private MovementType movementType;
    private Double amount;
    private MovementCategory category;
    private String description;
    private String voucherNumber;
    private Double previousBalance;
    private Double newBalance;

    public boolean isIncome() {
        return MovementType.IN.equals(movementType);
    }

    public boolean isExpense() {
        return MovementType.OUT.equals(movementType);
    }

    public boolean isAdjustment() {
        return MovementType.ADJUSTMENT.equals(movementType);
    }
}