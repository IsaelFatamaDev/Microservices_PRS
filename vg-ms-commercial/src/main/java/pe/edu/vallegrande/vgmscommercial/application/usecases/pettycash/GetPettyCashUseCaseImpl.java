package pe.edu.vallegrande.vgmscommercial.application.usecases.pettycash;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.models.PettyCash;
import pe.edu.vallegrande.vgmscommercial.domain.models.PettyCashMovement;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.pettycash.IGetPettyCashUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IPettyCashRepository;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.PettyCashNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetPettyCashUseCaseImpl implements IGetPettyCashUseCase {

     private final IPettyCashRepository pettyCashRepository;

     @Override
     public Mono<PettyCash> findById(String id, String organizationId) {
          log.debug("Finding petty cash by id: {}", id);
          return pettyCashRepository.findById(id)
                    .filter(pc -> pc.getOrganizationId().equals(organizationId))
                    .switchIfEmpty(Mono.error(new PettyCashNotFoundException(id)));
     }

     @Override
     public Flux<PettyCash> findAll(String organizationId, String status, Integer page, Integer size) {
          log.debug("Finding petty cash for organization: {}", organizationId);
          Flux<PettyCash> pettyCashes = pettyCashRepository.findByOrganizationId(organizationId);
          if (status != null && !status.isEmpty()) {
               pettyCashes = pettyCashes.filter(pc -> pc.getPettyCashStatus().name().equals(status));
          }
          if (page != null && size != null) {
               pettyCashes = pettyCashes.skip((long) page * size).take(size);
          }
          return pettyCashes;
     }

     @Override
     public Mono<PettyCash> findActiveByOrganization(String organizationId) {
          log.debug("Finding active petty cash for organization: {}", organizationId);
          return pettyCashRepository.findActiveByOrganizationId(organizationId);
     }

     @Override
     public Flux<PettyCashMovement> findMovements(String pettyCashId, String organizationId) {
          log.debug("Finding movements for petty cash: {}", pettyCashId);
          return pettyCashRepository.findById(pettyCashId)
                    .filter(pc -> pc.getOrganizationId().equals(organizationId))
                    .switchIfEmpty(Mono.error(new PettyCashNotFoundException(pettyCashId)))
                    .thenMany(pettyCashRepository.findMovementsByPettyCashId(pettyCashId));
     }

     @Override
     public Mono<Long> count(String organizationId, String status) {
          return pettyCashRepository.countByOrganizationIdAndStatus(organizationId, status);
     }
}
