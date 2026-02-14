package pe.edu.vallegrande.vgmswaterquality.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(
        basePackages = "pe.edu.vallegrande.vgmswaterquality.infrastructure.persistence.repositories"
)
public class MongoConfig {
}
