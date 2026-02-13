package pe.edu.vallegrande.vgmswaterquality.application.usecases.testingpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmswaterquality.domain.exceptions.TestingPointNotFoundException;
import pe.edu.vallegrande.vgmswaterquality.domain.models.TestingPoint;
import pe.edu.vallegrande.vgmswaterquality.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.in.testingpoint.IRestoreTestingPointUseCase;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.out.ITestingPointRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestoreTestingPointUseCaseImpl implements IRestoreTestingPointUseCase {

    private final ITestingPointRepository repository;

    @Override
    public Mono<TestingPoint> execute(String id, String restoredBy) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new TestingPointNotFoundException(id)))
                .flatMap(existing -> {
                    existing.setRecordStatus(RecordStatus.ACTIVE.name());
                    existing.setUpdatedAt(LocalDateTime.now());
                    existing.setUpdatedBy(restoredBy);
                    return repository.update(existing);
                })
                .doOnSuccess(p -> log.info("TestingPoint restaurado: {}", p.getId()))
                .doOnError(e -> log.error("Error restaurando TestingPoint: {}", e.getMessage()));
    }
}
