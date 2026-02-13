package pe.edu.vallegrande.vgmscommercial.domain.ports.out;

import pe.edu.vallegrande.vgmscommercial.domain.models.*;

public interface ICommercialEventPublisher {
    void publishReceiptCreated(Receipt receipt, String createdBy);
    void publishReceiptPaid(Receipt receipt, String paidBy);
    void publishPaymentCreated(Payment payment, String createdBy);
    void publishPaymentCancelled(Payment payment, String cancelledBy);
    void publishDebtCreated(Debt debt, String createdBy);
    void publishDebtPaid(Debt debt, String paidBy);
    void publishServiceCutScheduled(ServiceCut serviceCut, String scheduledBy);
    void publishServiceCutExecuted(ServiceCut serviceCut, String executedBy);
    void publishServiceReconnected(ServiceCut serviceCut, String reconnectedBy);
}