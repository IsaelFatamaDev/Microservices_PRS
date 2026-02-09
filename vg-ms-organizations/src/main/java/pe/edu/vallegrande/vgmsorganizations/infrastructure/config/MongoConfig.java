package pe.edu.vallegrande.vgmsorganizations.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(
    basePackages = "pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.repositories"
)
public class MongoConfig {
}
