package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

        @Bean
        @Profile("dev")
        public SecurityWebFilterChain devSecurityFilterChain(ServerHttpSecurity http) {
                return http
                                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                                .authorizeExchange(exchanges -> exchanges
                                                .anyExchange().permitAll())
                                .build();
        }

        @Bean
        @Profile("prod")
        public SecurityWebFilterChain prodSecurityFilterChain(ServerHttpSecurity http) {
                return http
                                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                                .authorizeExchange(exchanges -> exchanges
                                                .pathMatchers("/actuator/**").permitAll()
                                                .pathMatchers("/webjars/**").permitAll()
                                                .anyExchange().permitAll())
                                .build();
        }
}
