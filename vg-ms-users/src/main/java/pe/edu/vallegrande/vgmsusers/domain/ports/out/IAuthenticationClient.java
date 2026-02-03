package pe.edu.vallegrande.vgmsusers.domain.ports.out;

import reactor.core.publisher.Mono;

public interface IAuthenticationClient {

    Mono<String> createUser(String userId, String email, String firstName, String lastName, String role);

    Mono<Void> disableUser(String userId);

    Mono<Void> enableUser(String userId);

    Mono<Void> deleteUser(String userId);
}
