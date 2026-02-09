package pe.edu.vallegrande.vgmsnotifications.domain.ports.out;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface IWhatsAppClient {

    Mono<Boolean> sendTextMessage(String phoneNumber, String message);

    Mono<Boolean> sendImageMessage(String phoneNumber, String imageUrl, String caption);

    Mono<Boolean> isConnected();
}
