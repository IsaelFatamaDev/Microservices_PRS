package pe.edu.vallegrande.vgmsusers.domain.ports.out;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface INotificationClient {
    Mono<Void> sendWelcomeMessage(String phone, String firstName, String organizationName);

    Mono<Void> sendProfileUpdatedNotification(String phone, String firstName);}
