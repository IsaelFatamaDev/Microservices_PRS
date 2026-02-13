package pe.edu.vallegrande.vgmscommercial.application.usecases.receipt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.models.Receipt;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.receipt.IUpdateReceiptUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IReceiptRepository;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ICommercialEventPublisher;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.ReceiptNotFoundException;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.ReceiptAlreadyPaidException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateReceiptUseCaseImpl implements IUpdateReceiptUseCase {

     private final IReceiptRepository receiptRepository;
     private final ICommercialEventPublisher eventPublisher;

     @Override
     public Mono<Receipt> execute(String id, Receipt receipt) {
          log.info("Updating receipt: {}", id);
          return receiptRepository.findById(id)
                    .switchIfEmpty(Mono.error(new ReceiptNotFoundException(id)))
                    .flatMap(existing -> {
                         if (existing.isPaid()) {
                              return Mono.error(new ReceiptAlreadyPaidException(id));
                         }
                         Receipt updated = existing.toBuilder()
                                   .notes(receipt.getNotes())
                                   .dueDate(receipt.getDueDate())
                                   .updatedAt(LocalDateTime.now())
                                   .updatedBy(receipt.getUpdatedBy())
                                   .build();
                         return receiptRepository.save(updated);
                    })
                    .doOnSuccess(saved -> log.info("Receipt updated successfully: {}", saved.getReceiptNumber()));
     }
}
