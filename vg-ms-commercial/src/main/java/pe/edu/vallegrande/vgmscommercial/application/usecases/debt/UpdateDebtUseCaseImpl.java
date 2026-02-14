package pe.edu.vallegrande.vgmscommercial.application.usecases.debt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.models.Debt;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.debt.IUpdateDebtUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IDebtRepository;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ICommercialEventPublisher;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.DebtNotFoundException;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.base.BusinessRuleException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateDebtUseCaseImpl implements IUpdateDebtUseCase {

     private final IDebtRepository debtRepository;
     private final ICommercialEventPublisher eventPublisher;

     @Override
     public Mono<Debt> execute(String id, Debt debt) {
          log.info("Updating debt: {}", id);
          return debtRepository.findById(id)
                    .switchIfEmpty(Mono.error(new DebtNotFoundException(id)))
                    .flatMap(existing -> {
                         // Allow updating to PAID status
                         boolean isMarkingAsPaid = debt.getDebtStatus() != null &&
                                   debt.getDebtStatus().name().equals("PAID");

                         if (existing.isPaid() && !isMarkingAsPaid) {
                              return Mono.error(new BusinessRuleException("Cannot update a paid debt"));
                         }

                         var builder = existing.toBuilder()
                                   .pendingAmount(debt.getPendingAmount() != null ? debt.getPendingAmount()
                                             : existing.getPendingAmount())
                                   .lateFee(debt.getLateFee() != null ? debt.getLateFee() : existing.getLateFee())
                                   .dueDate(debt.getDueDate() != null ? debt.getDueDate() : existing.getDueDate())
                                   .updatedAt(LocalDateTime.now())
                                   .updatedBy(debt.getUpdatedBy());

                         // Update debtStatus if provided
                         if (debt.getDebtStatus() != null) {
                              builder.debtStatus(debt.getDebtStatus());
                              log.info("Updating debt {} status to: {}", id, debt.getDebtStatus());
                         }

                         Debt updated = builder.build();
                         return debtRepository.save(updated);
                    })
                    .doOnSuccess(saved -> log.info("Debt updated successfully: {} - Status: {}",
                              saved.getId(), saved.getDebtStatus()));
     }
}
