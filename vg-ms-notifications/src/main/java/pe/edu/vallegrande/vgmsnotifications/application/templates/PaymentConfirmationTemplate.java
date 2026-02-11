package pe.edu.vallegrande.vgmsnotifications.application.templates;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects.NotificationType;
import java.util.Map;

@Component
public class PaymentConfirmationTemplate implements MessageTemplate {

    @Override
    public NotificationType getType() {
        return NotificationType.PAYMENT_CONFIRMATION;
    }

    @Override
    public String render(Map<String, String> variables) {
        String name = variables.getOrDefault("name", "Usuario");
        String amount = variables.getOrDefault("amount", "0.00");
        String date = variables.getOrDefault("date", "N/A");
        String receiptCode = variables.getOrDefault("receiptCode", "N/A");
        String period = variables.getOrDefault("period", "N/A");

        return String.format("""
                💰 *Pago Confirmado*

                Estimado/a *%s*, su pago ha sido registrado exitosamente.

                📋 Detalle:
                • Monto: S/ %s
                • Fecha: %s
                • Recibo: %s
                • Período: %s

                ¡Gracias por su puntualidad!

                _Sistema de Gestión para Juntas Administradoras de Servicios de Saneamiento (JASS)_ 💧
                """, name, amount, date, receiptCode, period);
    }
}
