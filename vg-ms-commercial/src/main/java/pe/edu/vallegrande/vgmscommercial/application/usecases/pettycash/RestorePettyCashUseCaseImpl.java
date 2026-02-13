package pe.edu.vallegrande.vgmscommercial.application.usecases.pettycash;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.models.PettyCash;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.pettycash.IRestorePettyCashUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IPettyCashRepository;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ISecurityContext;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.PettyCashNotFoundException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestorePettyCashUseCaseImpl implements IRestorePettyCashUseCase {

     private final IPettyCashRepository pettyCashRepository;
     private final ISecurityContext securityContext;

     @Override
     public Mono<PettyCash> execute(String id, String organizationId) {
          log.info("Restoring petty cash: {}", id);
          return pettyCashRepository.findById(id)
                    .filter(pc -> pc.getOrganizationId().equals(organizationId))
                    .switchIfEmpty(Mono.error(new PettyCashNotFoundException(id)))
                    .flatMap(pettyCash -> securityContext.getCurrentUserId()
                              .flatMap(userId -> {
                                   var restored = pettyCash.markAsActive(userId);
                                   return pettyCashRepository.save(restored);
                              }))
                    .doOnSuccess(saved -> log.info("Petty cash restored: {}", saved.getId()));
     }
}
