package pe.edu.vallegrande.vgmsnotifications.application.templates;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;
import java.util.Map;

@Component
public class OverdueReminderTemplate implements MessageTemplate {

    @Override
    public NotificationType getType() {
        return NotificationType.OVERDUE_REMINDER;
    }

    @Override
    public String render(Map<String, String> variables) {
        String name = variables.getOrDefault("name", "Usuario");
        String amount = variables.getOrDefault("amount", "0.00");
        String period = variables.getOrDefault("period", "N/A");
        String monthsOverdue = variables.getOrDefault("monthsOverdue", "N/A");

        return String.format("""
                ⚠️ *Pago Vencido — Aviso Importante*

                Estimado/a *%s*, tiene pagos pendientes.

                📋 Detalle:
                • Monto adeudado: S/ %s
                • Período: %s
                • Meses sin pagar: %s

                ⏰ *Recuerde que al acumular 3 meses sin pagar, se procederá con el corte del servicio de agua.*

                Regularice su situación lo antes posible.

                _Sistema de Gestión para Juntas Administradoras de Servicios de Saneamiento (JASS)_ 💧
                """, name, amount, period, monthsOverdue);
    }
}
