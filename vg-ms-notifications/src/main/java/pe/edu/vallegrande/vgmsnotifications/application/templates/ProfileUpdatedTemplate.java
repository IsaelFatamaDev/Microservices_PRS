package pe.edu.vallegrande.vgmsnotifications.application.templates;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;

import java.util.Map;

@Component
public class ProfileUpdatedTemplate implements MessageTemplate {

     @Override
     public NotificationType getType() {
          return NotificationType.PROFILE_UPDATED;
     }

     @Override
     public String render(Map<String, String> variables) {
          String name = variables.getOrDefault("firstName", "Usuario");
          return String.format("""
                    ✅ *Profile Updated*

                    Hello 👤 *%s*, your profile has been successfully updated.

                    If you did not make this change, please contact support immediately.

                    _JASS Water Management System_ 💧
                    """, name);
     }
}
