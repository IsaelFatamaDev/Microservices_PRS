package pe.edu.vallegrande.vgmsnotifications.domain.exceptions;

import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;

public class TemplateNotFoundException extends DomainException {

    public TemplateNotFoundException(NotificationType type) {
        super("TEMPLATE_NOT_FOUND", 500, "Template not found for type: " + type.name());
    }
}
