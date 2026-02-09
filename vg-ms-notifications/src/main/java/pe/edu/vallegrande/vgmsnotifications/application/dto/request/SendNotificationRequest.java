package pe.edu.vallegrande.vgmsnotifications.application.dto.request;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "El número de teléfono es requerido")
    @Pattern(regexp = "^\\+?\\d{10,15}$", message = "El número de teléfono debe ser válido")
    private String phoneNumber;

    @NotBlank(message = "El nombre del destinatario es requerido")
    private String recipientName;

    @NotBlank(message = "el tipo de notificacion es requerido")
    private NotificationType type;

    private Map<String, String> variables;

    private String userId;
}
