package pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out;

import reactor.core.publisher.Mono;

public interface IInventoryEventPublisher {
    Mono<Void> publishMaterialCreated(String materialId, String materialCode, String createdBy);

    Mono<Void> publishMaterialUpdated(String materialId, String updatedBy);

    Mono<Void> publishMaterialDeleted(String materialId, String deletedBy);

    Mono<Void> publishPurchaseCreated(String purchaseId, String purchaseCode, String createdBy);

    Mono<Void> publishPurchaseReceived(String purchaseId, String receivedBy);

    Mono<Void> publishStockUpdated(String materialId, Double previousStock, Double newStock, String updatedBy);

    Mono<Void> publishLowStockAlert(String materialId, String materialCode, Double currentStock, Double minStock);
}
