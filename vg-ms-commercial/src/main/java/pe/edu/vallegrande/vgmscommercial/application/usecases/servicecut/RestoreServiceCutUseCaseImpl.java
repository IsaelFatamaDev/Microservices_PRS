package pe.edu.vallegrande.vgmscommercial.application.usecases.servicecut;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.models.ServiceCut;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.servicecut.IRestoreServiceCutUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IServiceCutRepository;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ISecurityContext;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.ServiceCutNotFoundException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestoreServiceCutUseCaseImpl implements IRestoreServiceCutUseCase {

     private final IServiceCutRepository serviceCutRepository;
     private final ISecurityContext securityContext;

     @Override
     public Mono<ServiceCut> execute(String id, String organizationId) {
          log.info("Restoring service cut: {}", id);
          return serviceCutRepository.findById(id)
                    .filter(sc -> sc.getOrganizationId().equals(organizationId))
                    .switchIfEmpty(Mono.error(new ServiceCutNotFoundException(id)))
                    .flatMap(serviceCut -> securityContext.getCurrentUserId()
                              .flatMap(userId -> {
                                   var restored = serviceCut.markAsActive(userId);
                                   return serviceCutRepository.save(restored);
                              }))
                    .doOnSuccess(saved -> log.info("Service cut restored: {}", saved.getId()));
     }
}
