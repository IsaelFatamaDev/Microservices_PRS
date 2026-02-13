package pe.edu.vallegrande.vgmscommercial.application.usecases.servicecut;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.models.ServiceCut;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.servicecut.ICreateServiceCutUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IServiceCutRepository;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.ICommercialEventPublisher;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IInfrastructureClient;
import pe.edu.vallegrande.vgmscommercial.domain.services.CommercialAuthorizationService;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.base.BusinessRuleException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateServiceCutUseCaseImpl implements ICreateServiceCutUseCase {

     private final IServiceCutRepository serviceCutRepository;
     private final ICommercialEventPublisher eventPublisher;
     private final IInfrastructureClient infrastructureClient;
     private final CommercialAuthorizationService authorizationService;

     @Override
     public Mono<ServiceCut> execute(ServiceCut serviceCut) {
          log.info("Creating service cut for user: {}", serviceCut.getUserId());

          return serviceCutRepository.existsActiveByUserId(serviceCut.getUserId())
                    .flatMap(exists -> {
                         if (exists) {
                              return Mono.error(new BusinessRuleException("User already has an active service cut"));
                         }
                         return infrastructureClient.existsWaterBox(serviceCut.getWaterBoxId());
                    })
                    .flatMap(exists -> {
                         if (!exists) {
                              return Mono.error(new BusinessRuleException(
                                        "Water box not found: " + serviceCut.getWaterBoxId()));
                         }
                         return authorizationService.canScheduleServiceCut(serviceCut.getUserId(),
                                   serviceCut.getOrganizationId());
                    })
                    .flatMap(canCut -> {
                         if (!canCut) {
                              return Mono.error(new BusinessRuleException(
                                        "User does not meet the debt threshold for service cut"));
                         }
                         return serviceCutRepository.save(serviceCut);
                    })
                    .doOnSuccess(saved -> {
                         log.info("Service cut scheduled: {}", saved.getId());
                         eventPublisher.publishServiceCutScheduled(saved, saved.getCreatedBy());
                    });
     }
}
