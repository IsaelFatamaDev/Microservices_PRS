package pe.edu.vallegrande.vgmsusers.domain.ports.in;

import pe.edu.vallegrande.vgmsusers.domain.models.User;
import reactor.core.publisher.Mono;

public interface IUpdateUserUseCase {

    Mono<User> execute(String id, User user, String updatedBy);
}
