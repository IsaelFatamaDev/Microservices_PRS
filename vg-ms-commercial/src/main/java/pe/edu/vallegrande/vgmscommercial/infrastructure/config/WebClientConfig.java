package pe.edu.vallegrande.vgmscommercial.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

     @Value("${webclient.services.user.base-url}")
     private String userServiceUrl;

     @Value("${webclient.services.infrastructure.base-url}")
     private String infrastructureServiceUrl;

     @Value("${webclient.services.notification.base-url}")
     private String notificationServiceUrl;

     @Value("${webclient.services.organization.base-url:http://localhost:8082}")
     private String organizationServiceUrl;

     @Bean("userWebClient")
     public WebClient userWebClient(WebClient.Builder builder) {
          return builder.baseUrl(userServiceUrl).build();
     }

     @Bean("infrastructureWebClient")
     public WebClient infrastructureWebClient(WebClient.Builder builder) {
          return builder.baseUrl(infrastructureServiceUrl).build();
     }

     @Bean("notificationWebClient")
     public WebClient notificationWebClient(WebClient.Builder builder) {
          return builder.baseUrl(notificationServiceUrl).build();
     }

     @Bean("organizationWebClient")
     public WebClient organizationWebClient(WebClient.Builder builder) {
          return builder.baseUrl(organizationServiceUrl).build();
     }
}
