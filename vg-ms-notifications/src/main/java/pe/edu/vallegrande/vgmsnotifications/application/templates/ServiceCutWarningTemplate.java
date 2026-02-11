package pe.edu.vallegrande.vgmsnotifications.application.templates;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;
import java.util.Map;

@Component
public class ServiceCutWarningTemplate implements MessageTemplate {

    @Override
    public NotificationType getType() {
        return NotificationType.SERVICE_CUT_WARNING;
    }

    @Override
    public String render(Map<String, String> variables) {
        String name = variables.getOrDefault("name", "Usuario");
        String cutDate = variables.getOrDefault("cutDate", "N/A");
        String debtAmount = variables.getOrDefault("debtAmount", "0.00");

        return String.format("""
                🚨 *Aviso de Corte de Servicio*

                Estimado/a *%s*, debido a la deuda pendiente de S/ %s, se procederá con el corte del servicio de agua.

                📅 Fecha programada de corte: *%s*

                ⚠️ Tiene 5 días para regularizar su pago y evitar la suspensión.

                _Sistema de Gestión para Juntas Administradoras de Servicios de Saneamiento (JASS)_ 💧
                """, name, debtAmount, cutDate);
    }
}
