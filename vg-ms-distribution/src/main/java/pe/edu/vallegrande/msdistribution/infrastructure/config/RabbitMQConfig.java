package pe.edu.vallegrande.msdistribution.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el microservicio de distribución.
 * Define el exchange, queues y bindings para los eventos de distribución.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "jass.events";
    
    // Queues
    private static final String PROGRAM_QUEUE = "distribution.program.events.queue";
    private static final String ROUTE_QUEUE = "distribution.route.events.queue";
    private static final String SCHEDULE_QUEUE = "distribution.schedule.events.queue";
    
    // Routing keys patterns
    private static final String PROGRAM_ROUTING_KEY = "distribution.program.*";
    private static final String ROUTE_ROUTING_KEY = "distribution.route.*";
    private static final String SCHEDULE_ROUTING_KEY = "distribution.schedule.*";

    /**
     * Exchange principal para todos los eventos del ecosistema JASS.
     * Es compartido entre todos los microservicios.
     */
    @Bean
    public TopicExchange jassEventsExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    // ========== PROGRAM QUEUES AND BINDINGS ==========
    
    @Bean
    public Queue programEventsQueue() {
        return QueueBuilder.durable(PROGRAM_QUEUE).build();
    }
    
    @Bean
    public Binding programBinding(Queue programEventsQueue, TopicExchange jassEventsExchange) {
        return BindingBuilder.bind(programEventsQueue)
                .to(jassEventsExchange)
                .with(PROGRAM_ROUTING_KEY);
    }
    
    // ========== ROUTE QUEUES AND BINDINGS ==========
    
    @Bean
    public Queue routeEventsQueue() {
        return QueueBuilder.durable(ROUTE_QUEUE).build();
    }
    
    @Bean
    public Binding routeBinding(Queue routeEventsQueue, TopicExchange jassEventsExchange) {
        return BindingBuilder.bind(routeEventsQueue)
                .to(jassEventsExchange)
                .with(ROUTE_ROUTING_KEY);
    }
    
    // ========== SCHEDULE QUEUES AND BINDINGS ==========
    
    @Bean
    public Queue scheduleEventsQueue() {
        return QueueBuilder.durable(SCHEDULE_QUEUE).build();
    }
    
    @Bean
    public Binding scheduleBinding(Queue scheduleEventsQueue, TopicExchange jassEventsExchange) {
        return BindingBuilder.bind(scheduleEventsQueue)
                .to(jassEventsExchange)
                .with(SCHEDULE_ROUTING_KEY);
    }

    /**
     * Convertidor de mensajes a JSON usando Jackson.
     * Permite serializar/deserializar eventos como JSON.
     */
    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
