package pe.edu.vallegrande.vgmsnotifications.application.templates;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;

import java.util.Map;

@Component
public class ReceiptNotificationTemplate implements MessageTemplate {

    @Override
    public NotificationType getType() {
        return NotificationType.RECEIPT_NOTIFICATION;
    }

    @Override
    public String render(Map<String, String> variables) {
        String name = variables.getOrDefault("name", "Usuario");
        String receiptCode = variables.getOrDefault("receiptCode", "N/A");
        String period = variables.getOrDefault("period", "N/A");
        String totalAmount = variables.getOrDefault("totalAmount", "0.00");
        String dueDate = variables.getOrDefault("dueDate", "N/A");

        return String.format("""
                🧾 *Recibo Generado*

                Estimado/a *%s*, se ha generado su recibo de agua.

                📋 Detalle:
                • Recibo: %s
                • Período: %s
                • Monto total: S/ %s
                • Vencimiento: %s

                Realice su pago antes de la fecha de vencimiento.

                _Sistema de Gestión para Juntas Administradoras de Servicios de Saneamiento (JASS)_ 💧
                """, name, receiptCode, period, totalAmount, dueDate);
    }
}
