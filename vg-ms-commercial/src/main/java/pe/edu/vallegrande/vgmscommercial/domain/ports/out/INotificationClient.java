package pe.edu.vallegrande.vgmscommercial.domain.ports.out;

import reactor.core.publisher.Mono;

public interface INotificationClient {
    Mono<Void> sendPaymentConfirmation(String userId, Double amount, String receiptNumber);
    Mono<Void> sendServiceCutWarning(String userId, Double debtAmount, Integer daysTocut);
    Mono<Void> sendServiceCutExecuted(String userId);
    Mono<Void> sendServiceReconnected(String userId);
}