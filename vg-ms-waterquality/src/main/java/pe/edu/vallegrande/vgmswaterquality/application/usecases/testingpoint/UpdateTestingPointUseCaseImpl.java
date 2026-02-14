package pe.edu.vallegrande.vgmswaterquality.application.usecases.testingpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmswaterquality.domain.exceptions.TestingPointNotFoundException;
import pe.edu.vallegrande.vgmswaterquality.domain.models.TestingPoint;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.in.testingpoint.IUpdateTestingPointUseCase;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.out.ITestingPointRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateTestingPointUseCaseImpl implements IUpdateTestingPointUseCase {

    private final ITestingPointRepository repository;

    @Override
    public Mono<TestingPoint> execute(String id, TestingPoint point, String updatedBy) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new TestingPointNotFoundException(id)))
                .flatMap(existing -> {
                    existing.setOrganizationId(point.getOrganizationId());
                    existing.setZoneId(point.getZoneId());
                    existing.setPointName(point.getPointName());
                    existing.setPointType(point.getPointType());
                    existing.setLocation(point.getLocation());
                    existing.setLatitude(point.getLatitude());
                    existing.setLongitude(point.getLongitude());
                    existing.setUpdatedAt(LocalDateTime.now());
                    existing.setUpdatedBy(updatedBy);
                    return repository.update(existing);
                })
                .doOnSuccess(p -> log.info("TestingPoint actualizado: {}", p.getId()))
                .doOnError(e -> log.error("Error actualizando TestingPoint: {}", e.getMessage()));
    }
}
