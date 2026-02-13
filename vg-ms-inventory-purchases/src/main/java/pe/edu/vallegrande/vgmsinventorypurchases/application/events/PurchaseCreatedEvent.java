package pe.edu.vallegrande.vgmsinventorypurchases.application.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseCreatedEvent {
    private String purchaseId;
    private String purchaseCode;
    private String createdBy;
    private LocalDateTime timestamp;
}
