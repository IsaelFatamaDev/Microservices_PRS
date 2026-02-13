package pe.edu.vallegrande.vgmsclaims.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.util.Collections;

/**
 * MongoDB configuration for reactive operations.
 */
@Configuration
@EnableReactiveMongoAuditing
@EnableReactiveMongoRepositories(basePackages = "pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.repositories")
public class MongoConfig {

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Collections.emptyList());
    }
}
