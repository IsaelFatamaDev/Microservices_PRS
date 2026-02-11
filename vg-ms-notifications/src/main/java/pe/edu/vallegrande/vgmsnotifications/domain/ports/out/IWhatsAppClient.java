package pe.edu.vallegrande.vgmsnotifications.domain.ports.out;

import reactor.core.publisher.Mono;

public interface IWhatsAppClient {

    Mono<Boolean> sendTextMessage(String phoneNumber, String message);

    Mono<Boolean> sendImageMessage(String phoneNumber, String imageUrl, String caption);

    Mono<Boolean> isConnected();
}
