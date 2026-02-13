package pe.edu.vallegrande.vgmsinfrastructure.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "jass.events";
    public static final String INFRASTRUCTURE_QUEUE = "infrastructure.events";
    public static final String COMMERCIAL_QUEUE = "commercial.events.infrastructure";
    public static final String CLAIMS_QUEUE = "claims.events.infrastructure";

    @Bean
    public TopicExchange jassEventsExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue infrastructureQueue() {
        return new Queue(INFRASTRUCTURE_QUEUE, true);
    }

    @Bean
    public Queue commercialEventsQueue() {
        return new Queue(COMMERCIAL_QUEUE, true);
    }

    @Bean
    public Queue claimsEventsQueue() {
        return new Queue(CLAIMS_QUEUE, true);
    }

    @Bean
    public Binding infrastructureBinding(Queue infrastructureQueue, TopicExchange jassEventsExchange) {
        return BindingBuilder.bind(infrastructureQueue).to(jassEventsExchange).with("waterbox.*");
    }

    @Bean
    public Binding commercialBinding(Queue commercialEventsQueue, TopicExchange jassEventsExchange) {
        return BindingBuilder.bind(commercialEventsQueue).to(jassEventsExchange).with("service-cut.*");
    }

    @Bean
    public Binding claimsBinding(Queue claimsEventsQueue, TopicExchange jassEventsExchange) {
        return BindingBuilder.bind(claimsEventsQueue).to(jassEventsExchange).with("incident.*");
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setExchange(EXCHANGE_NAME);
        return rabbitTemplate;
    }
}
