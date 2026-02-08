package pe.edu.vallegrande.vgmsauthentication.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "jass.events";

    public static final String USER_CREATED_QUEUE = "auth.user.created";
    public static final String USER_UPDATED_QUEUE = "auth.user.updated";
    public static final String USER_DELETED_QUEUE = "auth.user.deleted";
    public static final String USER_RESTORED_QUEUE = "auth.user.restored";
    public static final String USER_PURGED_QUEUE = "auth.user.purged";

    public static final String USER_CREATED_KEY = "user.created";
    public static final String USER_UPDATED_KEY = "user.updated";
    public static final String USER_DELETED_KEY = "user.deleted";
    public static final String USER_RESTORED_KEY = "user.restored";
    public static final String USER_PURGED_KEY = "user.purged";

    @Bean
    public TopicExchange jassEventsExchange() {
        return ExchangeBuilder
                .topicExchange(EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    @Bean
    public Queue userCreatedQueue() {
        return QueueBuilder.durable(USER_CREATED_QUEUE).build();
    }

    @Bean
    public Queue userUpdatedQueue() {
        return QueueBuilder.durable(USER_UPDATED_QUEUE).build();
    }

    @Bean
    public Queue userDeletedQueue() {
        return QueueBuilder.durable(USER_DELETED_QUEUE).build();
    }

    @Bean
    public Queue userRestoredQueue() {
        return QueueBuilder.durable(USER_RESTORED_QUEUE).build();
    }

    @Bean
    public Queue userPurgedQueue() {
        return QueueBuilder.durable(USER_PURGED_QUEUE).build();
    }

    @Bean
    public Binding userCreatedBinding() {
        return BindingBuilder.bind(userCreatedQueue())
                .to(jassEventsExchange())
                .with(USER_CREATED_KEY);
    }

    @Bean
    public Binding userUpdatedBinding() {
        return BindingBuilder.bind(userUpdatedQueue())
                .to(jassEventsExchange())
                .with(USER_UPDATED_KEY);
    }

    @Bean
    public Binding userDeletedBinding() {
        return BindingBuilder.bind(userDeletedQueue())
                .to(jassEventsExchange())
                .with(USER_DELETED_KEY);
    }

    @Bean
    public Binding userRestoredBinding() {
        return BindingBuilder.bind(userRestoredQueue())
                .to(jassEventsExchange())
                .with(USER_RESTORED_KEY);
    }

    @Bean
    public Binding userPurgedBinding() {
        return BindingBuilder.bind(userPurgedQueue())
                .to(jassEventsExchange())
                .with(USER_PURGED_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);

        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(DefaultJackson2JavaTypeMapper.TypePrecedence.INFERRED);
        typeMapper.setTrustedPackages("*");
        converter.setJavaTypeMapper(typeMapper);

        return converter;
    }

    @SuppressWarnings("null")
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
