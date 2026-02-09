package pe.edu.vallegrande.vgmsnotifications.domain.models.valueobjects;

import lombok.Getter;

@Getter
public enum NotificationType {

    WELCOME("Biendvenido/a al sistema"),
    ACCOUNT_DEACTIVATED("Cuenta desactivada"),
    ACCOUNT_RESTORED("Cuenta restaurada"),
    PAYMENT_CONFIRMATION("Confirmación de pago"),
    RECEIPT_NOTIFICATION("Notificacion de recibo"),
    OVERDUE_REMINDER("Recordatorio de pago vencido"),
    SERVICE_CUT_WARNING("Advertencia de corte"),
    SERVICE_CUT_EXECUTE("Corte Ejecutado"),
    SERVICE_RESTORED("Servicio Restaurado");
    private final String description;

    NotificationType(String description){
        this.description = description;
    }

    public boolean requiresImage(){
        return this == WELCOME;
    }

    public boolean isUrgent(){
        return this == SERVICE_CUT_WARNING || this == SERVICE_CUT_EXECUTE || this == OVERDUE_REMINDER;
    }
}
