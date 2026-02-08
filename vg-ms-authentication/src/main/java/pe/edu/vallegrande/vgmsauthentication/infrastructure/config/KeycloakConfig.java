package pe.edu.vallegrande.vgmsauthentication.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Bean
    public String keycloakIssuerUri() {
        return authServerUrl + "/realms/" + realm;
    }

    @Bean
    public String keycloakTokenUri() {
        return authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
    }

    @Bean
    public String keycloakJwksUri() {
        return authServerUrl + "/realms/" + realm + "/protocol/openid-connect/certs";
    }
}