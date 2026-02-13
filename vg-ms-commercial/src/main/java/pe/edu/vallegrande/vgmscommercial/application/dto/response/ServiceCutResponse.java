package pe.edu.vallegrande.vgmscommercial.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCutResponse {

     private String id;
     private String organizationId;
     private String recordStatus;
     private LocalDateTime createdAt;
     private String createdBy;
     private LocalDateTime updatedAt;
     private String updatedBy;
     private String userId;
     private String userFullName;
     private String waterBoxId;
     private LocalDateTime scheduledDate;
     private LocalDateTime executedDate;
     private String cutReason;
     private Double debtAmount;
     private LocalDateTime reconnectionDate;
     private Boolean reconnectionFeePaid;
     private String cutStatus;
     private String notes;
}
