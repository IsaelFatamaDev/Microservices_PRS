package pe.edu.vallegrande.vgmscommercial.application.usecases.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.models.Payment;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.payment.ICreatePaymentUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IPaymentRepository;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ICommercialEventPublisher;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IUserServiceClient;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.INotificationClient;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.base.BusinessRuleException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatePaymentUseCaseImpl implements ICreatePaymentUseCase {

     private final IPaymentRepository paymentRepository;
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
                    .doOnSuccess(saved -> {
                         log.info("Payment created successfully: {}", saved.getId());
                         eventPublisher.publishPaymentCreated(saved, saved.getCreatedBy());
                         notificationClient
                                   .sendPaymentConfirmation(saved.getUserId(), saved.getTotalAmount(),
                                             saved.getReceiptNumber())
                                   .subscribe();
                    });
     }
}
