package pe.edu.vallegrande.vgmsnotifications.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppMessageRequest {

     @NotBlank(message = "Phone number is required")
     private String phone;

     @NotBlank(message = "Template name is required")
     private String template;

     private Map<String, Object> params;
}
