package pe.edu.vallegrande.vgmscommercial.application.dto.request.pettycash;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePettyCashRequest {

     private String responsibleUserId;

     @DecimalMin(value = "0.01", message = "Max amount limit must be greater than 0")
     private Double maxAmountLimit;

     private String pettyCashStatus;
}
