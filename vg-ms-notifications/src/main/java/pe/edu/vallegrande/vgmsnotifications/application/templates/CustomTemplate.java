package pe.edu.vallegrande.vgmsnotifications.application.templates;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;

import java.util.Map;

@Component
public class CustomTemplate implements MessageTemplate {

     @Override
     public NotificationType getType() {
          return NotificationType.CUSTOM;
     }

     @Override
     public String render(Map<String, String> variables) {
          String message = variables.getOrDefault("message", "Notification from JASS");
          return String.format("""
                    📢 *Notification*

                    %s

                    _JASS Water Management System_ 💧
                    """, message);
     }
}
