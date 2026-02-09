package pe.edu.vallegrande.vgmsnotifications.application.templates;

import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;

import java.util.Map;

public interface MessageTemplate {

    NotificationType getType();
    String render(Map<String, String> variables);
}
