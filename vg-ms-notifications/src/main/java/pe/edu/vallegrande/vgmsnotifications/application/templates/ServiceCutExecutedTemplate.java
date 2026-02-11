package pe.edu.vallegrande.vgmsnotifications.application.templates;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;
import java.util.Map;

@Component
public class ServiceCutExecutedTemplate implements MessageTemplate {

    @Override
    public NotificationType getType() {
        return NotificationType.SERVICE_CUT_EXECUTED;
    }

    @Override
    public String render(Map<String, String> variables) {
        String name = variables.getOrDefault("name", "Usuario");
        String date = variables.getOrDefault("date", "N/A");
        String debtAmount = variables.getOrDefault("debtAmount", "0.00");

        return String.format("""
                🔴 *Servicio Suspendido*

                Estimado/a *%s*, el servicio de agua ha sido suspendido el %s.

                💰 Deuda pendiente: S/ %s

                Para restablecer el servicio, acérquese a las oficinas de su JASS con el comprobante de pago.

                _Sistema de Gestión para Juntas Administradoras de Servicios de Saneamiento (JASS)_ 💧
                """, name, date, debtAmount);
    }
}
