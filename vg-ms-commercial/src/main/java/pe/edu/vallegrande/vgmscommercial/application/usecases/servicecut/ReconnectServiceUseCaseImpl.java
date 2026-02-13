package pe.edu.vallegrande.vgmscommercial.application.usecases.servicecut;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.models.ServiceCut;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.servicecut.IReconnectServiceUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IServiceCutRepository;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ICommercialEventPublisher;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IInfrastructureClient;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.INotificationClient;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ISecurityContext;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.ServiceCutNotFoundException;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.base.BusinessRuleException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReconnectServiceUseCaseImpl implements IReconnectServiceUseCase {

     private final IServiceCutRepository serviceCutRepository;
     private final ICommercialEventPublisher eventPublisher;
     private final IInfrastructureClient infrastructureClient;
     private final INotificationClient notificationClient;
     private final ISecurityContext securityContext;

     @Override
     public Mono<ServiceCut> execute(String id, String organizationId) {
          log.info("Reconnecting service: {}", id);
          return serviceCutRepository.findById(id)
                    .filter(sc -> sc.getOrganizationId().equals(organizationId))
                    .switchIfEmpty(Mono.error(new ServiceCutNotFoundException(id)))
                    .flatMap(serviceCut -> {
                         if (!serviceCut.isExecuted()) {
                              return Mono.error(
                                        new BusinessRuleException("Service cut must be executed before reconnection"));
                         }
                         return securityContext.getCurrentUserId()
                                   .flatMap(userId -> {
                                        ServiceCut reconnected = serviceCut.reconnect(userId);
                                        return infrastructureClient
                                                  .updateWaterBoxStatus(serviceCut.getWaterBoxId(), "ACTIVE")
                                                  .then(serviceCutRepository.save(reconnected));
                                   });
                    })
                    .doOnSuccess(saved -> {
                         log.info("Service reconnected: {}", saved.getId());
                         eventPublisher.publishServiceReconnected(saved, saved.getUpdatedBy());
                         notificationClient.sendServiceReconnected(saved.getUserId()).subscribe();
                    });
     }
}
