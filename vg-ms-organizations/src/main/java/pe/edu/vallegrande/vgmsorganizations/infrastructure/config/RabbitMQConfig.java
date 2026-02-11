package pe.edu.vallegrande.vgmsorganizations.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SuppressWarnings("null")
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "jass.events";

    @Bean
    public TopicExchange jassEventsExchange() {
        return ExchangeBuilder
                .topicExchange(EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    @Bean
    public Queue organizationEventsQueue() {
        return QueueBuilder.durable("organization.events.queue").build();
    }

    @Bean
    public Queue zoneEventsQueue() {
        return QueueBuilder.durable("zone.events.queue").build();
    }

    @Bean
    public Queue streetEventsQueue() {
        return QueueBuilder.durable("street.events.queue").build();
    }

    @Bean
    public Queue fareEventsQueue() {
        return QueueBuilder.durable("fare.events.queue").build();
    }

    @Bean
    public Queue parameterEventsQueue() {
        return QueueBuilder.durable("parameter.events.queue").build();
    }

    @Bean
    public Binding organizationBinding(Queue organizationEventsQueue, TopicExchange jassEventsExchange) {
        return BindingBuilder.bind(organizationEventsQueue).to(jassEventsExchange).with("organization.*");
    }

    @Bean
    public Binding zoneBinding(Queue zoneEventsQueue, TopicExchange jassEventsExchange) {
        return BindingBuilder.bind(zoneEventsQueue).to(jassEventsExchange).with("zone.*");
    }

    @Bean
    public Binding streetBinding(Queue streetEventsQueue, TopicExchange jassEventsExchange) {
        return BindingBuilder.bind(streetEventsQueue).to(jassEventsExchange).with("street.*");
    }

    @Bean
    public Binding fareBinding(Queue fareEventsQueue, TopicExchange jassEventsExchange) {
        return BindingBuilder.bind(fareEventsQueue).to(jassEventsExchange).with("fare.*");
    }

    @Bean
    public Binding parameterBinding(Queue parameterEventsQueue, TopicExchange jassEventsExchange) {
        return BindingBuilder.bind(parameterEventsQueue).to(jassEventsExchange).with("parameter.*");
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2JsonMessageConverter());
        return template;
    }
}
