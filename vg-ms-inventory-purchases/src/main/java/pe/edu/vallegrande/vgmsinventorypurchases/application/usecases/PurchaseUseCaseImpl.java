package pe.edu.vallegrande.vgmsinventorypurchases.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.exceptions.BusinessRuleException;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.exceptions.ConflictException;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.exceptions.PurchaseNotFoundException;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.InventoryMovement;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.Purchase;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.PurchaseDetail;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.PurchaseStatus;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.in.IPurchaseUseCase;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out.IInventoryEventPublisher;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out.IInventoryMovementRepository;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out.IMaterialRepository;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out.IPurchaseDetailRepository;
import pe.edu.vallegrande.vgmsinventorypurchases.domain.ports.out.IPurchaseRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseUseCaseImpl implements IPurchaseUseCase {

        private final IPurchaseRepository purchaseRepository;
        private final IPurchaseDetailRepository purchaseDetailRepository;
        private final IMaterialRepository materialRepository;
        private final IInventoryMovementRepository inventoryMovementRepository;
        private final IInventoryEventPublisher eventPublisher;

        @Override
        public Mono<Purchase> create(Purchase purchase, String createdBy) {
                log.info("Creating purchase");

                // Generar código si no viene
                String purchaseCode = (purchase.getPurchaseCode() != null
                                && !purchase.getPurchaseCode().trim().isEmpty())
                                                ? purchase.getPurchaseCode()
                                                : generatePurchaseCode();

                return purchaseRepository.existsByPurchaseCode(purchaseCode)
                                .flatMap(exists -> {
                                        if (exists) {
                                                return Mono.error(new ConflictException(
                                                                "Purchase with code '" + purchaseCode
                                                                                + "' already exists"));
                                        }
                                        LocalDateTime now = LocalDateTime.now();

                                        // Calculate total amount from details
                                        double totalAmount = purchase.getDetails() != null
                                                        ? purchase.getDetails().stream()
                                                                        .mapToDouble(d -> d.getQuantity()
                                                                                        * d.getUnitPrice())
                                                                        .sum()
                                                        : (purchase.getTotalAmount() != null ? purchase.getTotalAmount()
                                                                        : 0.0);

                                        Purchase newPurchase = purchase.toBuilder()
                                                        .purchaseCode(purchaseCode)
                                                        .purchaseStatus(PurchaseStatus.PENDING)
                                                        .recordStatus(RecordStatus.ACTIVE)
                                                        .totalAmount(totalAmount)
                                                        .createdAt(now)
                                                        .createdBy(createdBy)
                                                        .updatedAt(now)
                                                        .updatedBy(createdBy)
                                                        .build();
                                        return purchaseRepository.save(newPurchase);
                                })
                                .flatMap(savedPurchase -> {
                                        if (purchase.getDetails() == null || purchase.getDetails().isEmpty()) {
                                                return Mono.just(savedPurchase);
                                        }
                                        return Flux.fromIterable(purchase.getDetails())
                                                        .map(detail -> PurchaseDetail.create(
                                                                        savedPurchase.getId(),
                                                                        detail.getMaterialId(),
                                                                        detail.getQuantity(),
                                                                        detail.getUnitPrice()))
                                                        .flatMap(purchaseDetailRepository::save)
                                                        .collectList()
                                                        .map(savedDetails -> savedPurchase.toBuilder()
                                                                        .details(savedDetails).build());
                                })
                                .flatMap(saved -> eventPublisher
                                                .publishPurchaseCreated(saved.getId(), saved.getPurchaseCode(),
                                                                createdBy)
                                                .thenReturn(saved))
                                .doOnSuccess(saved -> log.info("Purchase created successfully: {}", saved.getId()))
                                .doOnError(error -> log.error("Error creating purchase: {}", error.getMessage()));
        }

        @Override
        public Mono<Purchase> findById(String id) {
                log.debug("Finding purchase by ID: {}", id);
                return purchaseRepository.findById(id)
                                .switchIfEmpty(Mono.error(new PurchaseNotFoundException(id)))
                                .flatMap(purchase -> purchaseDetailRepository.findByPurchaseId(purchase.getId())
                                                .collectList()
                                                .map(details -> purchase.toBuilder().details(details).build()));
        }

        @Override
        public Flux<Purchase> findAll() {
                log.debug("Finding all purchases");
                return purchaseRepository.findAll();
        }

        @Override
        public Flux<Purchase> findAllActive() {
                log.debug("Finding all active purchases");
                return purchaseRepository.findByRecordStatus(RecordStatus.ACTIVE);
        }

        @Override
        public Flux<Purchase> findBySupplierId(String supplierId) {
                log.debug("Finding purchases by supplier: {}", supplierId);
                return purchaseRepository.findBySupplierId(supplierId);
        }

        @Override
        public Mono<Purchase> update(String id, Purchase changes, String updatedBy) {
                log.info("Updating purchase: {}", id);
                return purchaseRepository.findById(id)
                                .switchIfEmpty(Mono.error(new PurchaseNotFoundException(id)))
                                .map(existing -> existing.updateWith(changes, updatedBy))
                                .flatMap(purchaseRepository::save)
                                .doOnSuccess(saved -> log.info("Purchase updated successfully: {}", saved.getId()))
                                .doOnError(error -> log.error("Error updating purchase {}: {}", id,
                                                error.getMessage()));
        }

        @Override
        public Mono<Void> softDelete(String id, String deletedBy) {
                log.info("Soft deleting purchase: {}", id);
                return purchaseRepository.findById(id)
                                .switchIfEmpty(Mono.error(new PurchaseNotFoundException(id)))
                                .map(purchase -> purchase.markAsDeleted(deletedBy))
                                .flatMap(purchaseRepository::save)
                                .then()
                                .doOnSuccess(v -> log.info("Purchase soft deleted: {}", id))
                                .doOnError(error -> log.error("Error deleting purchase {}: {}", id,
                                                error.getMessage()));
        }

        @Override
        public Mono<Purchase> restore(String id, String restoredBy) {
                log.info("Restoring purchase: {}", id);
                return purchaseRepository.findById(id)
                                .switchIfEmpty(Mono.error(new PurchaseNotFoundException(id)))
                                .map(purchase -> purchase.restore(restoredBy))
                                .flatMap(purchaseRepository::save)
                                .doOnSuccess(saved -> log.info("Purchase restored: {}", saved.getId()))
                                .doOnError(error -> log.error("Error restoring purchase {}: {}", id,
                                                error.getMessage()));
        }

        @Override
        public Mono<Purchase> receive(String id, String receivedBy) {
                log.info("Receiving purchase: {}", id);
                return purchaseRepository.findById(id)
                                .switchIfEmpty(Mono.error(new PurchaseNotFoundException(id)))
                                .flatMap(purchase -> {
                                        if (purchase.getPurchaseStatus() != PurchaseStatus.PENDING) {
                                                return Mono.error(new BusinessRuleException(
                                                                "Purchase can only be received when status is PENDING. Current status: "
                                                                                + purchase.getPurchaseStatus()));
                                        }
                                        Purchase receivedPurchase = purchase.receive(receivedBy);
                                        return purchaseRepository.save(receivedPurchase)
                                                        .flatMap(saved -> purchaseDetailRepository
                                                                        .findByPurchaseId(saved.getId())
                                                                        .flatMap(detail -> updateMaterialStockOnReceive(
                                                                                        detail,
                                                                                        purchase.getOrganizationId(),
                                                                                        receivedBy))
                                                                        .then(Mono.just(saved)))
                                                        .flatMap(saved -> eventPublisher
                                                                        .publishPurchaseReceived(saved.getId(),
                                                                                        receivedBy)
                                                                        .thenReturn(saved));
                                })
                                .doOnSuccess(saved -> log.info("Purchase received successfully: {}", saved.getId()))
                                .doOnError(error -> log.error("Error receiving purchase {}: {}", id,
                                                error.getMessage()));
        }

        @Override
        public Mono<Purchase> cancel(String id, String cancelledBy) {
                log.info("Cancelling purchase: {}", id);
                return purchaseRepository.findById(id)
                                .switchIfEmpty(Mono.error(new PurchaseNotFoundException(id)))
                                .flatMap(purchase -> {
                                        if (purchase.getPurchaseStatus() != PurchaseStatus.PENDING) {
                                                return Mono.error(new BusinessRuleException(
                                                                "Purchase can only be cancelled when status is PENDING. Current status: "
                                                                                + purchase.getPurchaseStatus()));
                                        }
                                        return Mono.just(purchase.cancel(cancelledBy));
                                })
                                .flatMap(purchaseRepository::save)
                                .doOnSuccess(saved -> log.info("Purchase cancelled successfully: {}", saved.getId()))
                                .doOnError(error -> log.error("Error cancelling purchase {}: {}", id,
                                                error.getMessage()));
        }

        private Mono<Void> updateMaterialStockOnReceive(PurchaseDetail detail, String organizationId,
                        String receivedBy) {
                return materialRepository.findById(detail.getMaterialId())
                                .flatMap(material -> {
                                        Double previousStock = material.getCurrentStock() != null
                                                        ? material.getCurrentStock()
                                                        : 0.0;
                                        Double newStock = previousStock + detail.getQuantity();

                                        InventoryMovement movement = InventoryMovement.createEntry(
                                                        organizationId,
                                                        material.getId(),
                                                        detail.getQuantity(),
                                                        detail.getUnitPrice(),
                                                        previousStock,
                                                        detail.getPurchaseId(),
                                                        "PURCHASE",
                                                        receivedBy);

                                        return inventoryMovementRepository.save(movement)
                                                        .then(materialRepository.save(
                                                                        material.adjustStock(newStock, receivedBy)))
                                                        .flatMap(updatedMaterial -> {
                                                                Mono<Void> stockEvent = eventPublisher
                                                                                .publishStockUpdated(
                                                                                                material.getId(),
                                                                                                previousStock, newStock,
                                                                                                receivedBy);
                                                                if (updatedMaterial.isLowStock()) {
                                                                        return stockEvent.then(eventPublisher
                                                                                        .publishLowStockAlert(
                                                                                                        material.getId(),
                                                                                                        material.getMaterialCode(),
                                                                                                        updatedMaterial.getCurrentStock(),
                                                                                                        updatedMaterial.getMinStock()));
                                                                }
                                                                return stockEvent;
                                                        });
                                });
        }

        private String generatePurchaseCode() {
                return "COMP" + System.currentTimeMillis();
        }
}
