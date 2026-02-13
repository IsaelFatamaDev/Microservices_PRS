package pe.edu.vallegrande.vgmscommercial.application.usecases.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.models.Payment;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.payment.IGetPaymentUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IPaymentRepository;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.PaymentNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetPaymentUseCaseImpl implements IGetPaymentUseCase {

     private final IPaymentRepository paymentRepository;

     @Override
     public Mono<Payment> findById(String id, String organizationId) {
          log.debug("Finding payment by id: {}", id);
          return paymentRepository.findById(id)
                    .filter(p -> p.getOrganizationId().equals(organizationId))
                    .switchIfEmpty(Mono.error(new PaymentNotFoundException(id)));
     }

     @Override
     public Flux<Payment> findAll(String organizationId, String status, String userId, Integer page, Integer size) {
          log.debug("Finding payments for organization: {}", organizationId);
          Flux<Payment> payments;
          if (status != null && !status.isEmpty()) {
               payments = paymentRepository.findByOrganizationIdAndStatus(organizationId, status);
          } else {
               payments = paymentRepository.findByOrganizationId(organizationId);
          }
          if (userId != null && !userId.isEmpty()) {
               payments = payments.filter(p -> p.getUserId().equals(userId));
          }
          if (page != null && size != null) {
               payments = payments.skip((long) page * size).take(size);
          }
          return payments;
     }

     @Override
     public Flux<Payment> findByUserId(String userId, String organizationId) {
          log.debug("Finding payments for user: {}", userId);
          return paymentRepository.findByUserId(userId)
                    .filter(p -> p.getOrganizationId().equals(organizationId));
     }

     @Override
     public Mono<Long> count(String organizationId, String status) {
          return paymentRepository.countByOrganizationIdAndStatus(organizationId, status);
     }
}
