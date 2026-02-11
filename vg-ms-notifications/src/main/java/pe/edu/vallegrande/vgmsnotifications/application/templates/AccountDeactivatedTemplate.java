package pe.edu.vallegrande.vgmsnotifications.application.templates;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;

import java.util.Map;

@Component
public class AccountDeactivatedTemplate implements MessageTemplate {

    @Override
    public NotificationType getType() {
        return NotificationType.ACCOUNT_DEACTIVATED;
    }

    @Override
    public String render(Map<String, String> variables) {
        String name = variables.getOrDefault("name", "Usuario");
        String reason = variables.getOrDefault("reason", "Decisión administrativa");
        return String.format("""
                🚫 *Cuenta desactivada*

                Estimado/a 👤 *%s*, su cuenta ha sido desactivada.

                📝 Motivo: %s

                Si cree que esto es un error, contacte con la Junta Administrativa.

                _Sistema de Gestión para Juntas Administradoras de Servicios de Saneamiento (JASS)_ 💧
                """, name, reason);

    }
}
