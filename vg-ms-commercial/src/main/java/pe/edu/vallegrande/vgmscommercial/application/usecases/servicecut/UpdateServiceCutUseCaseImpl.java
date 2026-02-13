package pe.edu.vallegrande.vgmscommercial.application.usecases.servicecut;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmscommercial.domain.models.ServiceCut;
import pe.edu.vallegrande.vgmscommercial.domain.ports.in.servicecut.IUpdateServiceCutUseCase;
import pe.edu.vallegrande.vgmscommercial.domain.ports.out.IServiceCutRepository;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.ServiceCutNotFoundException;
import pe.edu.vallegrande.vgmscommercial.domain.exceptions.specific.ServiceCutAlreadyExecutedException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateServiceCutUseCaseImpl implements IUpdateServiceCutUseCase {

     private final IServiceCutRepository serviceCutRepository;

     @Override
     public Mono<ServiceCut> execute(String id, ServiceCut serviceCut) {
          log.info("Updating service cut: {}", id);
          return serviceCutRepository.findById(id)
                    .switchIfEmpty(Mono.error(new ServiceCutNotFoundException(id)))
                    .flatMap(existing -> {
                         if (existing.isExecuted()) {
                              return Mono.error(new ServiceCutAlreadyExecutedException(id));
                         }
                         ServiceCut updated = existing.toBuilder()
                                   .scheduledDate(serviceCut.getScheduledDate() != null ? serviceCut.getScheduledDate()
                                             : existing.getScheduledDate())
                                   .notes(serviceCut.getNotes())
                                   .updatedAt(LocalDateTime.now())
                                   .updatedBy(serviceCut.getUpdatedBy())
                                   .build();
                         return serviceCutRepository.save(updated);
                    })
                    .doOnSuccess(saved -> log.info("Service cut updated: {}", saved.getId()));
     }
}
