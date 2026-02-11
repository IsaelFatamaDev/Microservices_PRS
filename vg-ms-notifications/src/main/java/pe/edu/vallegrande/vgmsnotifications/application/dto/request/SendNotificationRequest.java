package pe.edu.vallegrande.vgmsnotifications.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?\\d{10,15}$", message = "Phone number must be valid")
    private String phoneNumber;

    @NotBlank(message = "Recipient name is required")
    private String recipientName;

    @NotNull(message = "Notification type is required")
    private NotificationType type;

    private Map<String, String> variables;

    private String userId;
}
