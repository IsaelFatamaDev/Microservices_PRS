package pe.edu.vallegrande.vgmscommercial.application.usecases.receipt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.models.Receipt;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.receipt.ICreateReceiptUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IReceiptRepository;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ICommercialEventPublisher;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IUserServiceClient;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.base.BusinessRuleException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateReceiptUseCaseImpl implements ICreateReceiptUseCase {

     private final IReceiptRepository receiptRepository;
     private final ICommercialEventPublisher eventPublisher;
     private final IUserServiceClient userServiceClient;

     @Override
     public Mono<Receipt> execute(Receipt receipt) {
          log.info("Creating receipt for user {} period {}/{}", receipt.getUserId(), receipt.getPeriodMonth(),
                    receipt.getPeriodYear());

          return userServiceClient.existsById(receipt.getUserId(), receipt.getOrganizationId())
                    .flatMap(exists -> {
                         if (!exists) {
                              return Mono.error(new BusinessRuleException("User not found: " + receipt.getUserId()));
                         }
                         return receiptRepository.existsByUserIdAndPeriod(receipt.getUserId(), receipt.getPeriodMonth(),
                                   receipt.getPeriodYear());
                    })
                    .flatMap(exists -> {
                         if (exists) {
                              return Mono.error(new BusinessRuleException(
                                        String.format("Receipt already exists for user %s period %02d/%d",
                                                  receipt.getUserId(), receipt.getPeriodMonth(),
                                                  receipt.getPeriodYear())));
                         }
                         return receiptRepository.save(receipt);
                    })
                    .doOnSuccess(saved -> {
                         log.info("Receipt created successfully: {}", saved.getReceiptNumber());
                         eventPublisher.publishReceiptCreated(saved, saved.getCreatedBy());
                    });
     }
}
