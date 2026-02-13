package pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out.IInventoryEventPublisher;
import pe.edu.vallegrande.vgmsinventorypurchases.infrastructure.config.RabbitMQConfig;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventPublisherImpl implements IInventoryEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public Mono<Void> publishMaterialCreated(String materialId, String materialCode, String createdBy) {
        return publishEvent("inventory.material.created", Map.of(
                "materialId", materialId,
                "materialCode", materialCode,
                "createdBy", createdBy,
                "timestamp", LocalDateTime.now().toString()));
    }

    @Override
    public Mono<Void> publishMaterialUpdated(String materialId, String updatedBy) {
        return publishEvent("inventory.material.updated", Map.of(
                "materialId", materialId,
                "updatedBy", updatedBy,
                "timestamp", LocalDateTime.now().toString()));
    }

    @Override
    public Mono<Void> publishMaterialDeleted(String materialId, String deletedBy) {
        return publishEvent("inventory.material.deleted", Map.of(
                "materialId", materialId,
                "deletedBy", deletedBy,
                "timestamp", LocalDateTime.now().toString()));
    }

    @Override
    public Mono<Void> publishPurchaseCreated(String purchaseId, String purchaseCode, String createdBy) {
        return publishEvent("inventory.purchase.created", Map.of(
                "purchaseId", purchaseId,
                "purchaseCode", purchaseCode,
                "createdBy", createdBy,
                "timestamp", LocalDateTime.now().toString()));
    }

    @Override
    public Mono<Void> publishPurchaseReceived(String purchaseId, String receivedBy) {
        return publishEvent("inventory.purchase.received", Map.of(
                "purchaseId", purchaseId,
                "receivedBy", receivedBy,
                "timestamp", LocalDateTime.now().toString()));
    }

    @Override
    public Mono<Void> publishStockUpdated(String materialId, Double previousStock, Double newStock, String updatedBy) {
        return publishEvent("inventory.stock.updated", Map.of(
                "materialId", materialId,
                "previousStock", previousStock,
                "newStock", newStock,
                "updatedBy", updatedBy,
                "timestamp", LocalDateTime.now().toString()));
    }

    @Override
    public Mono<Void> publishLowStockAlert(String materialId, String materialCode, Double currentStock,
            Double minStock) {
        return publishEvent("inventory.stock.low-alert", Map.of(
                "materialId", materialId,
                "materialCode", materialCode,
                "currentStock", currentStock,
                "minStock", minStock,
                "timestamp", LocalDateTime.now().toString()));
    }

    private Mono<Void> publishEvent(String routingKey, Object event) {
        return Mono.fromRunnable(() -> {
            try {
                rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, event);
                log.info("Event published successfully - routing key: {}", routingKey);
            } catch (Exception e) {
                log.error("Failed to publish event with routing key {}: {}", routingKey, e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
