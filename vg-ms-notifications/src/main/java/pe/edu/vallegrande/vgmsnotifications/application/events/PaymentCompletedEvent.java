package pe.edu.vallegrande.vgmsnotifications.application.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCompletedEvent {

    private String eventId;
    private String userId;
    private String clientName;
    private String phoneNumber;
    private String amount;
    private String paymentDate;
    private String receiptCode;
    private String period;
    private String timestamp;
}
