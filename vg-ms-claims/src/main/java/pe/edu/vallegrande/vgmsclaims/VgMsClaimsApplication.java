package pe.edu.vallegrande.vgmsclaims;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application for the Claims and Incidents microservice.
 *
 * This microservice manages:
 * - Customer complaints about water service
 * - Technical infrastructure incidents
 *
 * Architecture: Hexagonal (Ports & Adapters)
 * Framework: Spring Boot WebFlux (Reactive)
 * Database: MongoDB
 * Messaging: RabbitMQ
 */
@SpringBootApplication
public class VgMsClaimsApplication {

    public static void main(String[] args) {
        SpringApplication.run(VgMsClaimsApplication.class, args);
    }
}
