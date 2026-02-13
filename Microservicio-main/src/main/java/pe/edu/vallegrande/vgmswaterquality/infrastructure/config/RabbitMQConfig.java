package pe.edu.vallegrande.vgmswaterquality.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

     public static final String EXCHANGE_NAME = "jass.events";
     public static final String QUALITY_TEST_QUEUE = "water-quality.test.events.queue";
     public static final String TESTING_POINT_QUEUE = "water-quality.point.events.queue";

     @Bean
     public TopicExchange jassEventsExchange() {
          return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
     }

     @Bean
     public Queue qualityTestEventsQueue() {
          return QueueBuilder.durable(QUALITY_TEST_QUEUE).build();
     }

     @Bean
     public Queue testingPointEventsQueue() {
          return QueueBuilder.durable(TESTING_POINT_QUEUE).build();
     }

     @Bean
     public Binding qualityTestBinding(Queue qualityTestEventsQueue, TopicExchange jassEventsExchange) {
          return BindingBuilder.bind(qualityTestEventsQueue).to(jassEventsExchange).with("water-quality.test.*");
     }

     @Bean
     public Binding testingPointBinding(Queue testingPointEventsQueue, TopicExchange jassEventsExchange) {
          return BindingBuilder.bind(testingPointEventsQueue).to(jassEventsExchange).with("water-quality.point.*");
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
