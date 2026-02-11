package pe.edu.vallegrande.vgmsnotifications.application.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreatedEvent {

    private String eventType;
    private String eventId;
    private String timestamp;
    private String userId;
    private String organizationId;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String documentNumber;
    private String role;
    private String createdBy;
    private String correlationId;

    public String getFullName() {
        if (firstName == null && lastName == null) {
            return "User";
        }
        String first = firstName != null ? firstName : "";
        String last = lastName != null ? lastName : "";
        return (first + " " + last).trim();
    }
}
