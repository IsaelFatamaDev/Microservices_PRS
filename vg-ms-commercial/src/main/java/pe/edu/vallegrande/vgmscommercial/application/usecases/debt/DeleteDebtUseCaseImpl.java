package pe.edu.vallegrande.vgmscommercial.application.usecases.debt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.debt.IDeleteDebtUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IDebtRepository;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ICommercialEventPublisher;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ISecurityContext;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.DebtNotFoundException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteDebtUseCaseImpl implements IDeleteDebtUseCase {

     private final IDebtRepository debtRepository;
     private final ISecurityContext securityContext;

     @Override
     public Mono<Void> execute(String id, String organizationId) {
          log.info("Deleting debt: {}", id);
          return debtRepository.findById(id)
                    .filter(d -> d.getOrganizationId().equals(organizationId))
                    .switchIfEmpty(Mono.error(new DebtNotFoundException(id)))
                    .flatMap(debt -> securityContext.getCurrentUserId()
                              .flatMap(userId -> {
                                   var deleted = debt.markAsDeleted(userId);
                                   return debtRepository.save(deleted)
                                             .doOnSuccess(saved -> log.info("Debt deleted: {}", saved.getId()));
                              }))
                    .then();
     }
}
