package pe.edu.vallegrande.vgmsnotifications.application.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptGeneratedEvent {
    private String eventId;
    private String userId;
    private String clientName;
    private String phoneNumber;
    private String receiptCode;
    private String period;
    private String totalAmount;
    private String dueDate;
    private String timestamp;
}
