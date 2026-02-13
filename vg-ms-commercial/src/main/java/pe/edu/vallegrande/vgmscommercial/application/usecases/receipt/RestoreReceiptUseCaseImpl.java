package pe.edu.vallegrande.vgmscommercial.application.usecases.receipt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.models.Receipt;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.receipt.IRestoreReceiptUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IReceiptRepository;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ISecurityContext;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.ReceiptNotFoundException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestoreReceiptUseCaseImpl implements IRestoreReceiptUseCase {

     private final IReceiptRepository receiptRepository;
     private final ISecurityContext securityContext;

     @Override
     public Mono<Receipt> execute(String id, String organizationId) {
          log.info("Restoring receipt: {}", id);
          return receiptRepository.findById(id)
                    .filter(r -> r.getOrganizationId().equals(organizationId))
                    .switchIfEmpty(Mono.error(new ReceiptNotFoundException(id)))
                    .flatMap(receipt -> securityContext.getCurrentUserId()
                              .flatMap(userId -> {
                                   var restored = receipt.markAsActive(userId);
                                   return receiptRepository.save(restored);
                              }))
                    .doOnSuccess(saved -> log.info("Receipt restored: {}", saved.getReceiptNumber()));
     }
}
