package pe.edu.vallegrande.vgmscommercial.infrastructure.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

     @Bean
     public TopicExchange jassEventsExchange() {
          return new TopicExchange("jass.events", true, false);
     }

     @Bean
     public MessageConverter jackson2JsonMessageConverter() {
          return new Jackson2JsonMessageConverter();
     }
}
