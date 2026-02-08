package pe.edu.vallegrande.vgmsauthentication.domain.ports.out;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface IUserServiceClient {

    Mono<UserInfo> getUserById(String userId);

    Mono<UserInfo> getUserByEmail(String email);

    Mono<Boolean> existsUser(String userId);

    record UserInfo(
            String id,
            String organizationId,
            String email,
            String firstName,
            String lastName,
            String role) {
    }
}
