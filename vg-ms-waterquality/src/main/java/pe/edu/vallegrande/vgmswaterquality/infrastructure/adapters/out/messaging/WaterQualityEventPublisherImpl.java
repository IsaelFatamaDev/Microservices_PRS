package pe.edu.vallegrande.vgmswaterquality.infrastructure.adapters.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmswaterquality.domain.models.QualityTest;
import pe.edu.vallegrande.vgmswaterquality.domain.models.TestingPoint;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.out.IWaterQualityEventPublisher;
import pe.edu.vallegrande.vgmswaterquality.infrastructure.config.RabbitMQConfig;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WaterQualityEventPublisherImpl implements IWaterQualityEventPublisher {

     private final RabbitTemplate rabbitTemplate;

     @Override
     public Mono<Void> publishTestCreated(QualityTest test, String createdBy) {
          return publishEvent("water-quality.test.created",
                    buildTestEvent("QUALITY_TEST_CREATED", test.getId(), test.getOrganizationId(), createdBy));
     }

     @Override
     public Mono<Void> publishTestUpdated(QualityTest test, Map<String, Object> changedFields, String updatedBy) {
          Map<String, Object> event = buildTestEvent("QUALITY_TEST_UPDATED", test.getId(), test.getOrganizationId(),
                    updatedBy);
          event.put("changedFields", changedFields);
          return publishEvent("water-quality.test.updated", event);
     }

     @Override
     public Mono<Void> publishTestDeleted(String testId, String organizationId, String deletedBy, String reason) {
          Map<String, Object> event = buildTestEvent("QUALITY_TEST_DELETED", testId, organizationId, deletedBy);
          event.put("reason", reason);
          return publishEvent("water-quality.test.deleted", event);
     }

     @Override
     public Mono<Void> publishTestRestore(String testId, String organizationId, String restoredBy) {
          return publishEvent("water-quality.test.restored",
                    buildTestEvent("QUALITY_TEST_RESTORED", testId, organizationId, restoredBy));
     }

     @Override
     public Mono<Void> publishTestingCreated(TestingPoint point, String createdBy) {
          return publishEvent("water-quality.point.created",
                    buildPointEvent("TESTING_POINT_CREATED", point.getId(), point.getOrganizationId(), createdBy));
     }

     @Override
     public Mono<Void> publishTestingUpdated(TestingPoint point, Map<String, Object> changedFields, String updatedBy) {
          Map<String, Object> event = buildPointEvent("TESTING_POINT_UPDATED", point.getId(), point.getOrganizationId(),
                    updatedBy);
          event.put("changedFields", changedFields);
          return publishEvent("water-quality.point.updated", event);
     }

     @Override
     public Mono<Void> publishTestingDeleted(String pointId, TestingPoint point, String deletedBy) {
          return publishEvent("water-quality.point.deleted",
                    buildPointEvent("TESTING_POINT_DELETED", pointId, point.getOrganizationId(), deletedBy));
     }

     @Override
     public Mono<Void> publishTestingRestored(String pointId, String organizationId, String restoredBy) {
          return publishEvent("water-quality.point.restored",
                    buildPointEvent("TESTING_POINT_RESTORED", pointId, organizationId, restoredBy));
     }

     private Mono<Void> publishEvent(String routingKey, Map<String, Object> event) {
          return Mono.fromRunnable(() -> {
               rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, event);
               log.info("Evento publicado: {} - {}", routingKey, event.get("eventType"));
          })
                    .subscribeOn(Schedulers.boundedElastic())
                    .then();
     }

     private Map<String, Object> buildTestEvent(String eventType, String testId, String organizationId, String userId) {
          Map<String, Object> event = new HashMap<>();
          event.put("eventType", eventType);
          event.put("testId", testId);
          event.put("organizationId", organizationId);
          event.put("userId", userId);
          event.put("timestamp", LocalDateTime.now().toString());
          return event;
     }

     private Map<String, Object> buildPointEvent(String eventType, String pointId, String organizationId,
               String userId) {
          Map<String, Object> event = new HashMap<>();
          event.put("eventType", eventType);
          event.put("pointId", pointId);
          event.put("organizationId", organizationId);
          event.put("userId", userId);
          event.put("timestamp", LocalDateTime.now().toString());
          return event;
     }
}
