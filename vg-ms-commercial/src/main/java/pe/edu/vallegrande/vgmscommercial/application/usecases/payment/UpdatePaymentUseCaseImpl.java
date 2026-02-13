package pe.edu.vallegrande.vgmscommercial.application.usecases.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.models.Payment;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.payment.IUpdatePaymentUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IPaymentRepository;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ICommercialEventPublisher;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.PaymentNotFoundException;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.base.BusinessRuleException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdatePaymentUseCaseImpl implements IUpdatePaymentUseCase {

     private final IPaymentRepository paymentRepository;
     private final ICommercialEventPublisher eventPublisher;

     @Override
     public Mono<Payment> execute(String id, Payment payment) {
          log.info("Updating payment: {}", id);
          return paymentRepository.findById(id)
                    .switchIfEmpty(Mono.error(new PaymentNotFoundException(id)))
                    .flatMap(existing -> {
                         if (existing.isCancelled()) {
                              return Mono.error(new BusinessRuleException("Cannot update a cancelled payment"));
                         }
                         Payment updated = existing.toBuilder()
                                   .notes(payment.getNotes())
                                   .updatedAt(LocalDateTime.now())
                                   .updatedBy(payment.getUpdatedBy())
                                   .build();
                         return paymentRepository.save(updated);
                    })
                    .doOnSuccess(saved -> log.info("Payment updated successfully: {}", saved.getId()));
     }
}
