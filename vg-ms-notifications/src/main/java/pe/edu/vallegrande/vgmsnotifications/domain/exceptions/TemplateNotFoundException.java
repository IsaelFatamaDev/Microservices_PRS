package pe.edu.vallegrande.vgmsnotifications.domain.exceptions;

import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;

public class TemplateNotFoundException extends DomainException{

    public TemplateNotFoundException(NotificationType notificationType){
        super("TEMPLATE_NOT_FOUND", "No se encontró template para el tipo: " + notificationType.name());
    }
}
