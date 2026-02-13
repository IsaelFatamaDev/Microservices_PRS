package pe.edu.vallegrande.vgmscommercial.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.ConceptType;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDetail {

    private String id;
    private String receiptId;
    private ConceptType conceptType;
    private String description;
    private Double amount;
    private LocalDateTime createdAt;

    public boolean isMonthlyFee() {
        return ConceptType.MONTHLY_FEE.equals(conceptType);
    }

    public boolean isLateFee() {
        return ConceptType.LATE_FEE.equals(conceptType);
    }

    public boolean isFine() {
        return ConceptType.ASSEMBLY_FINE.equals(conceptType) || ConceptType.WORK_FINE.equals(conceptType);
    }
}