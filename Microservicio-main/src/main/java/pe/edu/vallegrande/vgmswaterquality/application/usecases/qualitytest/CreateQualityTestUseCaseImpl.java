package pe.edu.vallegrande.vgmswaterquality.application.usecases.qualitytest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmswaterquality.domain.models.QualityTest;
import pe.edu.vallegrande.vgmswaterquality.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.in.qualitytest.ICreateQualityTestUseCase;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.out.IQualityTestRepository;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.out.IWaterQualityEventPublisher;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateQualityTestUseCaseImpl implements ICreateQualityTestUseCase {

    private final IQualityTestRepository testRepository;
    private final IWaterQualityEventPublisher eventPublisher;

    @Override
    public Mono<QualityTest> execute(QualityTest quality, String createdBy) {
        quality.setRecordStatus(RecordStatus.ACTIVE.name());
        quality.setCreatedAt(LocalDateTime.now());
        quality.setCreatedBy(createdBy);
        quality.setUpdatedAt(LocalDateTime.now());
        quality.setTestDate(quality.getTestDate() != null ? quality.getTestDate() : LocalDateTime.now());

        return testRepository.save(quality)
                .flatMap(saved -> eventPublisher.publishTestCreated(saved, createdBy)
                        .thenReturn(saved)
                        .onErrorResume(e -> {
                            log.warn("Error publicando evento: {}", e.getMessage());
                            return Mono.just(saved);
                        }))
                .doOnSuccess(t -> log.info("QualityTest creado: {}", t.getId()))
                .doOnError(e -> log.error("Error creando QualityTest: {}", e.getMessage()));
    }
}
