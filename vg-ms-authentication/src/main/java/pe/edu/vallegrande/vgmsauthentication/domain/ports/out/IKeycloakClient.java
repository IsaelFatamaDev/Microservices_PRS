package pe.edu.vallegrande.vgmsauthentication.domain.ports.out;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface IKeycloakClient {

        Mono<Map<String, Object>> getTokenWithPassword(
                        String username,
                        String password,
                        String clientId);

        Mono<Map<String, Object>> refreshToken(String refreshToken, String clientId);

        Mono<Void> revokeToken(String refreshToken, String clientId);

        Mono<Map<String, Object>> introspectToken(
                        String token,
                        String clientId,
                        String clientSecret);

        Mono<Map<String, Object>> getUserInfo(String accessToken);

        Mono<String> createUser(
                        String userId,
                        String email,
                        String username,
                        String firstName,
                        String lastName,
                        String password,
                        String role,
                        String organizationId);

        Mono<Void> updateUser(
                        String userId,
                        String email,
                        String firstName,
                        String lastName);

        Mono<Void> disableUser(String userId);

        Mono<Void> enableUser(String userId);

        Mono<Void> deleteUser(String userId);

        Mono<Void> assignRole(String userId, String roleName);

        Mono<Void> removeRole(String userId, String roleName);

        Mono<Void> resetPassword(String userId, String newPassword, boolean temporary);

        Mono<Boolean> existsByUsername(String username);
}
