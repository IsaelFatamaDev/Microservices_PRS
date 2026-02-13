package pe.edu.vallegrande.vgmscommercial.application.usecases.receipt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.receipt.IDeleteReceiptUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IReceiptRepository;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ICommercialEventPublisher;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ISecurityContext;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.ReceiptNotFoundException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteReceiptUseCaseImpl implements IDeleteReceiptUseCase {

     private final IReceiptRepository receiptRepository;
     private final ICommercialEventPublisher eventPublisher;
     private final ISecurityContext securityContext;

     @Override
     public Mono<Void> execute(String id, String organizationId) {
          log.info("Deleting receipt: {}", id);
          return receiptRepository.findById(id)
                    .filter(r -> r.getOrganizationId().equals(organizationId))
                    .switchIfEmpty(Mono.error(new ReceiptNotFoundException(id)))
                    .flatMap(receipt -> securityContext.getCurrentUserId()
                              .flatMap(userId -> {
                                   var deleted = receipt.markAsDeleted(userId);
                                   return receiptRepository.save(deleted)
                                             .doOnSuccess(saved -> {
                                                  log.info("Receipt deleted: {}", saved.getReceiptNumber());
                                                  eventPublisher.publishReceiptPaid(saved, userId);
                                             });
                              }))
                    .then();
     }
}
