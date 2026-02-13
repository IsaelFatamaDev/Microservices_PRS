package pe.edu.vallegrande.vgmscommercial.application.dto.request.servicecut;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateServiceCutRequest {

     private LocalDateTime scheduledDate;

     private String cutStatus;

     private String notes;
}
