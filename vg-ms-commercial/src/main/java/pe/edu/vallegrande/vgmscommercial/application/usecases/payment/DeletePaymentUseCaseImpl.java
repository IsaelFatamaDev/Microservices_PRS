package pe.edu.vallegrande.vgmscommercial.application.usecases.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.payment.IDeletePaymentUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IPaymentRepository;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ICommercialEventPublisher;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ISecurityContext;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.PaymentNotFoundException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeletePaymentUseCaseImpl implements IDeletePaymentUseCase {

     private final IPaymentRepository paymentRepository;
     private final ICommercialEventPublisher eventPublisher;
     private final ISecurityContext securityContext;

     @Override
     public Mono<Void> execute(String id, String organizationId) {
          log.info("Deleting payment: {}", id);
          return paymentRepository.findById(id)
                    .filter(p -> p.getOrganizationId().equals(organizationId))
                    .switchIfEmpty(Mono.error(new PaymentNotFoundException(id)))
                    .flatMap(payment -> securityContext.getCurrentUserId()
                              .flatMap(userId -> {
                                   var deleted = payment.markAsDeleted(userId);
                                   return paymentRepository.save(deleted)
                                             .doOnSuccess(saved -> {
                                                  log.info("Payment deleted: {}", saved.getId());
                                                  eventPublisher.publishPaymentCancelled(saved, userId);
                                             });
                              }))
                    .then();
     }
}
