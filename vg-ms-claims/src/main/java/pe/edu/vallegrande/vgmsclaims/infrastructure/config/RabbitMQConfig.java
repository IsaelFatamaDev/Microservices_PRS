package pe.edu.vallegrande.vgmsclaims.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for asynchronous messaging.
 * Uses centralized exchange: jass.events
 */
@Configuration
public class RabbitMQConfig {

     public static final String EXCHANGE = "jass.events";

     // Exchange
     @Bean
     public TopicExchange jassEventsExchange() {
          return new TopicExchange(EXCHANGE, true, false);
     }

     // Queues
     @Bean
     public Queue complaintEventsQueue() {
          return QueueBuilder.durable("complaint.events.queue").build();
     }

     @Bean
     public Queue complaintResponseEventsQueue() {
          return QueueBuilder.durable("complaint.response.events.queue").build();
     }

     @Bean
     public Queue incidentEventsQueue() {
          return QueueBuilder.durable("incident.events.queue").build();
     }

     @Bean
     public Queue incidentUrgentQueue() {
          return QueueBuilder.durable("incident.urgent.queue").build();
     }

     // Bindings
     @Bean
     public Binding complaintBinding(Queue complaintEventsQueue, TopicExchange jassEventsExchange) {
          return BindingBuilder.bind(complaintEventsQueue)
                    .to(jassEventsExchange)
                    .with("complaint.*");
     }

     @Bean
     public Binding complaintResponseBinding(Queue complaintResponseEventsQueue, TopicExchange jassEventsExchange) {
          return BindingBuilder.bind(complaintResponseEventsQueue)
                    .to(jassEventsExchange)
                    .with("complaint.response.*");
     }

     @Bean
     public Binding incidentBinding(Queue incidentEventsQueue, TopicExchange jassEventsExchange) {
          return BindingBuilder.bind(incidentEventsQueue)
                    .to(jassEventsExchange)
                    .with("incident.*");
     }

     @Bean
     public Binding incidentUrgentBinding(Queue incidentUrgentQueue, TopicExchange jassEventsExchange) {
          return BindingBuilder.bind(incidentUrgentQueue)
                    .to(jassEventsExchange)
                    .with("incident.urgent.*");
     }

     // Message Converter
     @Bean
     public MessageConverter jsonMessageConverter() {
          return new Jackson2JsonMessageConverter();
     }

     // RabbitTemplate
     @Bean
     public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
          RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
          rabbitTemplate.setMessageConverter(jsonMessageConverter());
          return rabbitTemplate;
     }
}
