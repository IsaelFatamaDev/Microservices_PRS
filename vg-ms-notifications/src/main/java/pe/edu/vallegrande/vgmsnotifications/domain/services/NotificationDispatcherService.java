package pe.edu.vallegrande.vgmsnotifications.domain.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsnotifications.domain.exceptions.InvalidRecipientException;
import pe.edu.vallegrande.vgmsnotifications.domain.models.Notification;
import pe.edu.vallegrande.vgmsnotifications.domain.ports.out.IWhatsAppClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NotificationDispatcherService {

    private final IWhatsAppClient whatsAppClient;

    public Mono<Notification> dispatch(Notification notification){
        if(!notification.hasValidPhoneNumber()){
            return Mono.error(new InvalidRecipientException(
                "Número de teléfono inválido: " + notification.getPhoneNumber()
            ));
        }

        Mono<Boolean> sendResult;
        if(notification.hasImage()){
            sendResult = whatsAppClient.sendImageMessage(
                notification.getPhoneNumber(),
                notification.getImageUrl(),
                notification.getMessage()
            );
        }else {
            sendResult = whatsAppClient.sendTextMessage(
                notification.getPhoneNumber(),
                notification.getMessage()
            );
        }
        return sendResult
            .map(success ->{
                if (success){
                    notification.markAsSent();
                }else {
                    notification.markAsFailed("Evolution API retornó false");
                }
                return notification;
            })
            .onErrorResume(error ->{
                notification.markAsFailed(error.getMessage());
                return Mono.just(notification);
            });
    }
}
