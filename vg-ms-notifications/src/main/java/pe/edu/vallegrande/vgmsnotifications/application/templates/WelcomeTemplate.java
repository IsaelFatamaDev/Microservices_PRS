package pe.edu.vallegrande.vgmsnotifications.application.templates;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;

import java.util.Map;

@Component
public class WelcomeTemplate implements MessageTemplate {

    @Value("${frontend.url}")
    private String frontend;

    @Override
    public NotificationType getType() {
        return NotificationType.WELCOME;
    }

    @Override
    public String render(Map<String, String> variables) {

        String name = variables.getOrDefault("name", "Usuario");
        String organization = variables.getOrDefault("organization", "JASS");
        String frontendUrl = variables.getOrDefault("frontendUrl", frontend);
        String username = variables.getOrDefault("username", "");
        String password = variables.getOrDefault("password", "");

        StringBuilder message = new StringBuilder();
        message.append("*¡Bienvenido/a al Sistema Jass!*\n\n");
        message.append(
                String.format("\uD83D\uDC4B\uD83C\uDFFB Hola *%s*, tu cuenta ha sido creada exitosamente en *%s*.\n\n",
                        name, organization));

        if (!username.isEmpty() && !password.isEmpty()) {
            message.append("*Tus credenciales de acceso:*\n");
            message.append(String.format("\uD83D\uDC64 Usuario: `%s`\n", username));
            message.append(String.format("\uD83D\uDD11 Contraseña: `%s`\n\n", password));
            message.append("_Por seguridad, cambia tu contraseña después del primer acceso._\n\n");
        }

        message.append("Accede al sistema desde:\n");
        message.append(frontendUrl).append("\n\n");
        message.append("Si tienes alguna consulta, contacte con la Junta Administrativa.\n\n");
        message.append(
                "_Sistema de Gestión para Juntas Administradoras de Servicios de Saneamiento (JASS)_ \uD83D\uDCA7\n");

        return message.toString();
    }
}
