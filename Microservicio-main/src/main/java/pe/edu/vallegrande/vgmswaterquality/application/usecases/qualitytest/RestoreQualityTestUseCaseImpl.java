package pe.edu.vallegrande.vgmswaterquality.application.usecases.qualitytest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmswaterquality.domain.exceptions.QualityTestNotFoundException;
import pe.edu.vallegrande.vgmswaterquality.domain.models.QualityTest;
import pe.edu.vallegrande.vgmswaterquality.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.in.qualitytest.IRestoreQualityTestUseCase;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.out.IQualityTestRepository;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.out.IWaterQualityEventPublisher;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestoreQualityTestUseCaseImpl implements IRestoreQualityTestUseCase {

     private final IQualityTestRepository repository;
     private final IWaterQualityEventPublisher eventPublisher;

     @Override
     public Mono<QualityTest> execute(String id, String restoredBy) {
          return repository.findById(id)
                    .switchIfEmpty(Mono.error(new QualityTestNotFoundException(id)))
                    .flatMap(test -> {
                         test.setRecordStatus(RecordStatus.ACTIVE.name());
                         test.setUpdatedAt(LocalDateTime.now());
                         test.setUpdatedBy(restoredBy);
                         return repository.update(test);
                    })
                    .flatMap(test -> eventPublisher
                              .publishTestRestore(test.getId(), test.getOrganizationId(), restoredBy)
                              .thenReturn(test)
                              .onErrorResume(e -> {
                                   log.warn("Error publicando evento: {}", e.getMessage());
                                   return Mono.just(test);
                              }))
                    .doOnSuccess(t -> log.info("QualityTest restaurado: {}", t.getId()))
                    .doOnError(e -> log.error("Error restaurando QualityTest: {}", e.getMessage()));
     }
}
