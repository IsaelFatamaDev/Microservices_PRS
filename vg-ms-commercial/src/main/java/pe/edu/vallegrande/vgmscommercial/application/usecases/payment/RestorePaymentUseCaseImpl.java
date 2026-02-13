package pe.edu.vallegrande.vgmscommercial.application.usecases.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.models.Payment;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.payment.IRestorePaymentUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IPaymentRepository;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ISecurityContext;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.PaymentNotFoundException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestorePaymentUseCaseImpl implements IRestorePaymentUseCase {

     private final IPaymentRepository paymentRepository;
     private final ISecurityContext securityContext;

     @Override
     public Mono<Payment> execute(String id, String organizationId) {
          log.info("Restoring payment: {}", id);
          return paymentRepository.findById(id)
                    .filter(p -> p.getOrganizationId().equals(organizationId))
                    .switchIfEmpty(Mono.error(new PaymentNotFoundException(id)))
                    .flatMap(payment -> securityContext.getCurrentUserId()
                              .flatMap(userId -> {
                                   var restored = payment.markAsActive(userId);
                                   return paymentRepository.save(restored);
                              }))
                    .doOnSuccess(saved -> log.info("Payment restored: {}", saved.getId()));
     }
}
