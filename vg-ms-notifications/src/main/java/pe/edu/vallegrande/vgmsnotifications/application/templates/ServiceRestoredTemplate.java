package pe.edu.vallegrande.vgmsnotifications.application.templates;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;

import java.util.Map;

@Component
public class ServiceRestoredTemplate implements MessageTemplate {

    @Override
    public NotificationType getType() {
        return NotificationType.SERVICE_RESTORED;
    }

    @Override
    public String render(Map<String, String> variables) {
        String name = variables.getOrDefault("name", "Usuario");
        String date = variables.getOrDefault("date", "N/A");
        String amountPaid = variables.getOrDefault("amountPaid", "0.00");

        return String.format(
                """
                        🟢 *Servicio de Agua Restaurado*

                        Estimado/a *%s*, su servicio de agua ha sido restablecido exitosamente.

                        📋 Detalle:
                        • Fecha de reconexión: %s
                        • Monto cancelado: S/ %s

                        ¡Gracias por regularizar su situación! Recuerde mantener sus pagos al día para evitar futuras interrupciones.

                        _Sistema de Gestión para Juntas Administradoras de Servicios de Saneamiento (JASS)_ 💧
                        """,
                name, date, amountPaid);
    }
}
