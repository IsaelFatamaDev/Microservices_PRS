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
public class PettyCashResponse {

     private String id;
     private String organizationId;
     private String recordStatus;
     private LocalDateTime createdAt;
     private String createdBy;
     private LocalDateTime updatedAt;
     private String updatedBy;
     private String responsibleUserId;
     private String responsibleUserName;
     private Double currentBalance;
     private Double maxAmountLimit;
     private String pettyCashStatus;
}
