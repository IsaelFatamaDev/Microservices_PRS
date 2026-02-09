package pe.edu.vallegrande.vgmsnotifications.application.templates;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsnotifications.domain.exceptions.TemplateNotFoundException;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TemplateEngine {

    private final Map<NotificationType, MessageTemplate> templateMap;

    public TemplateEngine(List<MessageTemplate> templates) {
        this.templateMap = templates.stream()
            .collect(Collectors.toMap(MessageTemplate::getType, Function.identity()));
    }

    public String render(NotificationType type, Map<String, String> variables){
        MessageTemplate template = templateMap.get(type);
        if(template == null){
            throw new TemplateNotFoundException(type);
        }
        return template.render(variables);
    }
}
