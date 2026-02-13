package pe.edu.vallegrande.vgmscommercial.domain.ports.out;

import reactor.core.publisher.Mono;

public interface IUserServiceClient {
    Mono<Boolean> existsById(String userId, String organizationId);
    Mono<String> getUserFullName(String userId);
    Mono<String> getUserPhone(String userId);
}