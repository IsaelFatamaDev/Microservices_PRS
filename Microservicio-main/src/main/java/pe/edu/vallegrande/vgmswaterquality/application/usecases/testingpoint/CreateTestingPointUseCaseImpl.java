package pe.edu.vallegrande.vgmswaterquality.application.usecases.testingpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmswaterquality.domain.models.TestingPoint;
import pe.edu.vallegrande.vgmswaterquality.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.in.testingpoint.ICreateTestingPointUseCase;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.out.ITestingPointRepository;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.out.IWaterQualityEventPublisher;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateTestingPointUseCaseImpl implements ICreateTestingPointUseCase {

    private final ITestingPointRepository repository;
    private final IWaterQualityEventPublisher eventPublisher;

    @Override
    public Mono<TestingPoint> execute(TestingPoint point, String createdBy) {
        point.setId(UUID.randomUUID().toString());
        point.setRecordStatus(RecordStatus.ACTIVE.name());
        point.setCreatedAt(LocalDateTime.now());
        point.setCreatedBy(createdBy);
        point.setUpdatedAt(LocalDateTime.now());

        return repository.save(point)
                .flatMap(saved -> eventPublisher.publishTestingCreated(saved, createdBy)
                        .thenReturn(saved)
                        .onErrorResume(e -> {
                            log.warn("Error publicando evento: {}", e.getMessage());
                            return Mono.just(saved);
                        }))
                .doOnSuccess(p -> log.info("TestingPoint creado: {}", p.getId()))
                .doOnError(e -> log.error("Error creando TestingPoint: {}", e.getMessage()));
    }
}
