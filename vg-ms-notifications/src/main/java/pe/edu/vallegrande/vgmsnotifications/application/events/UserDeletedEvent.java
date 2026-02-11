package pe.edu.vallegrande.vgmsnotifications.application.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDeletedEvent {

    private String eventType;
    private String eventId;
    private String timestamp;
    private String userId;
    private String organizationId;
    private String previousStatus;
    private String reason;
    private String deletedBy;
    private String correlationId;

    public String getPhoneNumber() {
        return null;
    }

    public String getFullName() {
        return "User";
    }
}
