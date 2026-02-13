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
                         if (existing.isPaid()) {
                              return Mono.error(new BusinessRuleException("Cannot update a paid debt"));
                         }
                         Debt updated = existing.toBuilder()
                                   .pendingAmount(debt.getPendingAmount() != null ? debt.getPendingAmount()
                                             : existing.getPendingAmount())
                                   .lateFee(debt.getLateFee() != null ? debt.getLateFee() : existing.getLateFee())
                                   .dueDate(debt.getDueDate() != null ? debt.getDueDate() : existing.getDueDate())
                                   .updatedAt(LocalDateTime.now())
                                   .updatedBy(debt.getUpdatedBy())
                                   .build();
                         return debtRepository.save(updated);
                    })
                    .doOnSuccess(saved -> log.info("Debt updated successfully: {}", saved.getId()));
     }
}
