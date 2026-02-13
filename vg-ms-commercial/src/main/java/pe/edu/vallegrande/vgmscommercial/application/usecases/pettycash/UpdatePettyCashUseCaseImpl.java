package pe.edu.vallegrande.vgmscommercial.application.usecases.pettycash;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.models.PettyCash;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.pettycash.IUpdatePettyCashUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IPettyCashRepository;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.PettyCashNotFoundException;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.base.BusinessRuleException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdatePettyCashUseCaseImpl implements IUpdatePettyCashUseCase {

     private final IPettyCashRepository pettyCashRepository;

     @Override
     public Mono<PettyCash> execute(String id, PettyCash pettyCash) {
          log.info("Updating petty cash: {}", id);
          return pettyCashRepository.findById(id)
                    .switchIfEmpty(Mono.error(new PettyCashNotFoundException(id)))
                    .flatMap(existing -> {
                         if (existing.isClosed()) {
                              return Mono.error(new BusinessRuleException("Cannot update a closed petty cash"));
                         }
                         PettyCash updated = existing.toBuilder()
                                   .maxAmountLimit(pettyCash.getMaxAmountLimit() != null ? pettyCash.getMaxAmountLimit()
                                             : existing.getMaxAmountLimit())
                                   .responsibleUserId(
                                             pettyCash.getResponsibleUserId() != null ? pettyCash.getResponsibleUserId()
                                                       : existing.getResponsibleUserId())
                                   .updatedAt(LocalDateTime.now())
                                   .updatedBy(pettyCash.getUpdatedBy())
                                   .build();
                         return pettyCashRepository.save(updated);
                    })
                    .doOnSuccess(saved -> log.info("Petty cash updated: {}", saved.getId()));
     }
}
