package pe.edu.vallegrande.vgmscommercial.application.usecases.pettycash;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.pettycash.IDeletePettyCashUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IPettyCashRepository;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ISecurityContext;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.PettyCashNotFoundException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeletePettyCashUseCaseImpl implements IDeletePettyCashUseCase {

     private final IPettyCashRepository pettyCashRepository;
     private final ISecurityContext securityContext;

     @Override
     public Mono<Void> execute(String id, String organizationId) {
          log.info("Deleting petty cash: {}", id);
          return pettyCashRepository.findById(id)
                    .filter(pc -> pc.getOrganizationId().equals(organizationId))
                    .switchIfEmpty(Mono.error(new PettyCashNotFoundException(id)))
                    .flatMap(pettyCash -> securityContext.getCurrentUserId()
                              .flatMap(userId -> {
                                   var deleted = pettyCash.markAsDeleted(userId);
                                   return pettyCashRepository.save(deleted)
                                             .doOnSuccess(saved -> log.info("Petty cash deleted: {}", saved.getId()));
                              }))
                    .then();
     }
}
