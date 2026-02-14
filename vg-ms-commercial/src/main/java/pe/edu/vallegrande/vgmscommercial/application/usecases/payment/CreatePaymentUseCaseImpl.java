package pe.edu.vallegrande.vgmscommercial.application.usecases.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.models.Payment;
import pe.edu.vallegrande.vgmscommercial.domain.models.PettyCash;
import pe.edu.vallegrande.vgmscommercial.domain.models.PettyCashMovement;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.MovementCategory;
import pe.edu.vallegrande.vgmscommercial.domain.models.valueobjects.MovementType;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.payment.ICreatePaymentUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IPaymentRepository;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IPettyCashRepository;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ICommercialEventPublisher;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IUserServiceClient;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.INotificationClient;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.base.BusinessRuleException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatePaymentUseCaseImpl implements ICreatePaymentUseCase {

     private final IPaymentRepository paymentRepository;
     private final IPettyCashRepository pettyCashRepository;
     private final ICommercialEventPublisher eventPublisher;
     private final IUserServiceClient userServiceClient;
     private final INotificationClient notificationClient;

     @Override
     public Mono<Payment> execute(Payment payment) {
          log.info("Creating payment for user: {}", payment.getUserId());

          return userServiceClient.existsById(payment.getUserId(), payment.getOrganizationId())
                    .flatMap(exists -> {
                         if (!exists) {
                              return Mono.error(new BusinessRuleException("User not found: " + payment.getUserId()));
                         }
                         return paymentRepository.save(payment);
                    })
                    .flatMap(saved -> registerPettyCashIncome(saved).thenReturn(saved))
                    .doOnSuccess(saved -> {
                         log.info("Payment created successfully: {}", saved.getId());
                         eventPublisher.publishPaymentCreated(saved, saved.getCreatedBy());
                         notificationClient
                                   .sendPaymentConfirmation(saved.getUserId(), saved.getTotalAmount(),
                                             saved.getReceiptNumber())
                                   .subscribe();
                    });
     }

     private Mono<Void> registerPettyCashIncome(Payment payment) {
          return pettyCashRepository.findActiveByOrganizationId(payment.getOrganizationId())
                    .flatMap(pettyCash -> {
                         PettyCash updated = pettyCash.addFunds(payment.getTotalAmount(), payment.getCreatedBy());
                         PettyCashMovement movement = PettyCashMovement.builder()
                                   .id(UUID.randomUUID().toString())
                                   .organizationId(payment.getOrganizationId())
                                   .createdAt(LocalDateTime.now())
                                   .createdBy(payment.getCreatedBy())
                                   .pettyCashId(pettyCash.getId())
                                   .movementDate(LocalDateTime.now())
                                   .movementType(MovementType.IN)
                                   .amount(payment.getTotalAmount())
                                   .category(MovementCategory.OTHER)
                                   .description("Ingreso por pago " + payment.getReceiptNumber())
                                   .voucherNumber(payment.getReceiptNumber())
                                   .previousBalance(pettyCash.getCurrentBalance())
                                   .newBalance(updated.getCurrentBalance())
                                   .build();
                         return pettyCashRepository.save(updated)
                                   .then(pettyCashRepository.saveMovement(movement))
                                   .then();
                    })
                    .onErrorResume(e -> {
                         log.warn("No active petty cash found for organization {}: {}",
                                   payment.getOrganizationId(), e.getMessage());
                         return Mono.empty();
                    });
     }
}
