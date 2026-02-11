package pe.edu.vallegrande.vgmsnotifications.application.templates;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;

import java.util.Map;

@Component
public class AccountRestoredTemplate implements MessageTemplate {

    @Value("${frontend.url}")
    private String frontend;

    @Override
    public NotificationType getType() {
        return NotificationType.ACCOUNT_RESTORED;
    }

    @Override
    public String render(Map<String, String> variables) {
        String name = variables.getOrDefault("name", "Usuario");
        String frontendUrl = variables.getOrDefault("frontendUrl", frontend);
        return String.format("""
                ✅ *Cuenta restaurada*
                Estimado/a *%s*, tu cuenta ha sido restaurada.

                Ya puede acceder nuevamente al sistema:
                %s

                _Sistema de Gestión para Juntas Administradoras de Servicios de Saneamiento (JASS)_ \uD83D\uDCA7
                """, name, frontendUrl);
    }
}
