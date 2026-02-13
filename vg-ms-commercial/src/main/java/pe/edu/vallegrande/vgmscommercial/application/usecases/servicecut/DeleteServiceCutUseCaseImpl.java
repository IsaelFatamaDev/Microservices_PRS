package pe.edu.vallegrande.vgmscommercial.application.usecases.servicecut;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.servicecut.IDeleteServiceCutUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IServiceCutRepository;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ISecurityContext;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.ServiceCutNotFoundException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteServiceCutUseCaseImpl implements IDeleteServiceCutUseCase {

     private final IServiceCutRepository serviceCutRepository;
     private final ISecurityContext securityContext;

     @Override
     public Mono<Void> execute(String id, String organizationId) {
          log.info("Deleting service cut: {}", id);
          return serviceCutRepository.findById(id)
                    .filter(sc -> sc.getOrganizationId().equals(organizationId))
                    .switchIfEmpty(Mono.error(new ServiceCutNotFoundException(id)))
                    .flatMap(serviceCut -> securityContext.getCurrentUserId()
                              .flatMap(userId -> {
                                   var deleted = serviceCut.markAsDeleted(userId);
                                   return serviceCutRepository.save(deleted)
                                             .doOnSuccess(saved -> log.info("Service cut deleted: {}", saved.getId()));
                              }))
                    .then();
     }
}
