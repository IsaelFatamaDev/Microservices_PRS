package pe.edu.vallegrande.vgmsauthentication.infrastructure.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsauthentication.infrastructure.adapters.out.external.KeycloakClientImpl;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakInitializer {

     private final KeycloakClientImpl keycloakClient;

     @Value("${keycloak.client-id}")
     private String clientId;

     @PostConstruct
     public void init() {
          log.info("Initializing Keycloak client mappers for: {}", clientId);

          keycloakClient.configureClientMappers(clientId)
                    .doOnSuccess(v -> log.info("Keycloak mappers configured successfully"))
                    .doOnError(error -> log.error("Failed to configure Keycloak mappers: {}", error.getMessage()))
                    .subscribe();
     }
}
