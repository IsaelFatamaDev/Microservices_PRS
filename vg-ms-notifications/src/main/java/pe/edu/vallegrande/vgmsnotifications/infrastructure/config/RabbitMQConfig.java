package pe.edu.vallegrande.vgmsnotifications.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "jass.events";

    public static final String RK_USER_CREATED = "user.created";
    public static final String RK_USER_DELETED = "user.deleted";
    public static final String RK_USER_RESTORED = "user.restored";
    public static final String RK_PAYMENT_COMPLETED = "payment.completed";
    public static final String RK_PAYMENT_OVERDUE = "payment.overdue";
    public static final String RK_RECEIPT_GENERATED = "receipt.generated";
    public static final String RK_SERVICE_CUT_WARNING = "service.cut.warning";
    public static final String RK_SERVICE_CUT_EXECUTED = "service.cut.executed";
    public static final String RK_SERVICE_RESTORED = "service.restored";

    public static final String QUEUE_USER_CREATED = "notification.user.created";
    public static final String QUEUE_USER_DELETED = "notification.user.deleted";
    public static final String QUEUE_USER_RESTORED = "notification.user.restored";
    public static final String QUEUE_PAYMENT_COMPLETED = "notification.payment.completed";
    public static final String QUEUE_PAYMENT_OVERDUE = "notification.payment.overdue";
    public static final String QUEUE_RECEIPT_GENERATED = "notification.receipt.generated";
    public static final String QUEUE_SERVICE_CUT_WARNING = "notification.service.cut.warning";
    public static final String QUEUE_SERVICE_CUT_EXECUTED = "notification.service.cut.executed";
    public static final String QUEUE_SERVICE_RESTORED = "notification.service.restored";


    @Bean
    public TopicExchange jassEventExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue userCreatedQueue() {
        return QueueBuilder.durable(QUEUE_USER_CREATED).build();
    }

    @Bean
    public Queue userDeletedQueue() {
        return QueueBuilder.durable(QUEUE_USER_DELETED).build();
    }

    @Bean
    public Queue userRestoredQueue() {
        return QueueBuilder.durable(QUEUE_USER_RESTORED).build();
    }

    @Bean
    public Queue paymentCompletedQueue() {
        return QueueBuilder.durable(QUEUE_PAYMENT_COMPLETED).build();
    }

    @Bean
    public Queue paymentOverdueQueue() {
        return QueueBuilder.durable(QUEUE_PAYMENT_OVERDUE).build();
    }

    @Bean
    public Queue receiptGeneratedQueue() {
        return QueueBuilder.durable(QUEUE_RECEIPT_GENERATED).build();
    }

    @Bean
    public Queue serviceCutWarningQueue() {
        return QueueBuilder.durable(QUEUE_SERVICE_CUT_WARNING).build();
    }

    @Bean
    public Queue serviceCutExecutedQueue() {
        return QueueBuilder.durable(QUEUE_SERVICE_CUT_EXECUTED).build();
    }

    @Bean
    public Queue serviceRestoredQueue() {
        return QueueBuilder.durable(QUEUE_SERVICE_RESTORED).build();
    }

    @Bean
    public Binding bindUserCreated(){
        return BindingBuilder.bind(userCreatedQueue()).to(jassEventExchange()).with(RK_USER_CREATED);
    }

    @Bean
    public Binding bindUserDeleted(){
        return BindingBuilder.bind(userDeletedQueue()).to(jassEventExchange()).with(RK_USER_DELETED);
    }

    @Bean
    public Binding bindUserRestored(){
        return BindingBuilder.bind(userRestoredQueue()).to(jassEventExchange()).with(RK_USER_RESTORED);
    }

    @Bean
    public Binding bindPaymentCompleted(){
        return BindingBuilder.bind(paymentCompletedQueue()).to(jassEventExchange()).with(RK_PAYMENT_COMPLETED);
    }

    @Bean
    public Binding bindPaymentOverdue(){
        return BindingBuilder.bind(paymentOverdueQueue()).to(jassEventExchange()).with(RK_PAYMENT_OVERDUE);
    }

    @Bean
    public Binding bindReceiptGenerated(){
        return BindingBuilder.bind(receiptGeneratedQueue()).to(jassEventExchange()).with(RK_RECEIPT_GENERATED);
    }

    @Bean
    public Binding bindServiceCutWarning(){
        return BindingBuilder.bind(serviceCutWarningQueue()).to(jassEventExchange()).with(RK_SERVICE_CUT_WARNING);
    }

    @Bean
    public Binding bindServiceCutExecuted(){
        return BindingBuilder.bind(serviceCutExecutedQueue()).to(jassEventExchange()).with(RK_SERVICE_CUT_EXECUTED);
    }

    @Bean
    public Binding bindServiceRestored(){
        return BindingBuilder.bind(serviceRestoredQueue()).to(jassEventExchange()).with(RK_SERVICE_RESTORED);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(DefaultJackson2JavaTypeMapper.TypePrecedence.INFERRED);
        typeMapper.addTrustedPackages("*");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter());
        return factory;
    }
}
