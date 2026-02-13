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
public class PurchaseReceivedEvent {
    private String purchaseId;
    private String receivedBy;
    private LocalDateTime timestamp;
}
