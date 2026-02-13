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
public class PaymentDetail {

    private String id;
    private String paymentId;
    private ConceptType paymentType;
    private String description;
    private Double amount;
    private Integer periodMonth;
    private Integer periodYear;
    private LocalDateTime createdAt;

    public String getPeriodDescription() {
        if (periodMonth != null && periodYear != null) {
            return String.format("%02d/%d", periodMonth, periodYear);
        }
        return "N/A";
    }
}