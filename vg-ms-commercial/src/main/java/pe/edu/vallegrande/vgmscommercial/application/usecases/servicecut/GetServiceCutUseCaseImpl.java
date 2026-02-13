package pe.edu.vallegrande.vgmscommercial.application.usecases.servicecut;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.models.ServiceCut;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.servicecut.IGetServiceCutUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IServiceCutRepository;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.ServiceCutNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetServiceCutUseCaseImpl implements IGetServiceCutUseCase {

     private final IServiceCutRepository serviceCutRepository;

     @Override
     public Mono<ServiceCut> findById(String id, String organizationId) {
          log.debug("Finding service cut by id: {}", id);
          return serviceCutRepository.findById(id)
                    .filter(sc -> sc.getOrganizationId().equals(organizationId))
                    .switchIfEmpty(Mono.error(new ServiceCutNotFoundException(id)));
     }

     @Override
     public Flux<ServiceCut> findAll(String organizationId, String status, String userId, Integer page, Integer size) {
          log.debug("Finding service cuts for organization: {}", organizationId);
          Flux<ServiceCut> cuts;
          if (status != null && !status.isEmpty()) {
               cuts = serviceCutRepository.findByOrganizationIdAndStatus(organizationId, status);
          } else {
               cuts = serviceCutRepository.findByOrganizationId(organizationId);
          }
          if (userId != null && !userId.isEmpty()) {
               cuts = cuts.filter(sc -> sc.getUserId().equals(userId));
          }
          if (page != null && size != null) {
               cuts = cuts.skip((long) page * size).take(size);
          }
          return cuts;
     }

     @Override
     public Flux<ServiceCut> findByUserId(String userId, String organizationId) {
          log.debug("Finding service cuts for user: {}", userId);
          return serviceCutRepository.findByUserId(userId)
                    .filter(sc -> sc.getOrganizationId().equals(organizationId));
     }

     @Override
     public Flux<ServiceCut> findPending(String organizationId) {
          log.debug("Finding pending service cuts for organization: {}", organizationId);
          return serviceCutRepository.findPendingByOrganizationId(organizationId);
     }

     @Override
     public Mono<Long> count(String organizationId, String status) {
          return serviceCutRepository.countByOrganizationIdAndStatus(organizationId, status);
     }
}
