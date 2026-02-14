package pe.edu.vallegrande.vgmswaterquality.application.usecases.qualitytest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmswaterquality.domain.exceptions.QualityTestNotFoundException;
import pe.edu.vallegrande.vgmswaterquality.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.in.qualitytest.IDeleteQualityTestUseCase;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.out.IQualityTestRepository;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.out.IWaterQualityEventPublisher;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteQualityTestUseCaseImpl implements IDeleteQualityTestUseCase {

     private final IQualityTestRepository repository;
     private final IWaterQualityEventPublisher eventPublisher;

     @Override
     public Mono<Void> execute(String id, String deletedBy) {
          return repository.findById(id)
                    .switchIfEmpty(Mono.error(new QualityTestNotFoundException(id)))
                    .flatMap(test -> {
                         test.setRecordStatus(RecordStatus.INACTIVE.name());
                         test.setUpdatedAt(LocalDateTime.now());
                         test.setUpdatedBy(deletedBy);
                         return repository.update(test);
                    })
                    .flatMap(test -> eventPublisher
                              .publishTestDeleted(test.getId(), test.getOrganizationId(), deletedBy, "Soft delete")
                              .onErrorResume(e -> {
                                   log.warn("Error publicando evento: {}", e.getMessage());
                                   return Mono.empty();
                              }))
                    .then()
                    .doOnSuccess(v -> log.info("QualityTest eliminado (lógico): {}", id))
                    .doOnError(e -> log.error("Error eliminando QualityTest: {}", e.getMessage()));
     }
}
