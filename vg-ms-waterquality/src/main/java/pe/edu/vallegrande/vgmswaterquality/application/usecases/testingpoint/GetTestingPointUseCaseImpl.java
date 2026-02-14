package pe.edu.vallegrande.vgmswaterquality.application.usecases.testingpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmswaterquality.domain.exceptions.TestingPointNotFoundException;
import pe.edu.vallegrande.vgmswaterquality.domain.models.TestingPoint;
import pe.edu.vallegrande.vgmswaterquality.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.in.testingpoint.IGetTestingPointUseCase;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.out.ITestingPointRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetTestingPointUseCaseImpl implements IGetTestingPointUseCase {

    private final ITestingPointRepository repository;

    @Override
    public Mono<TestingPoint> findByAll(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new TestingPointNotFoundException(id)));
    }

    @Override
    public Flux<TestingPoint> findAll() {
        return repository.findAll();
    }

    @Override
    public Flux<TestingPoint> findAllActive() {
        return repository.findByRecordStatus(RecordStatus.ACTIVE.name());
    }

    @Override
    public Flux<TestingPoint> findByOrganizationId(String organizationId) {
        return repository.findByOrganizationId(organizationId);
    }

    @Override
    public Flux<TestingPoint> findActiveByOrganizationId(String organizationId) {
        return repository.findByOrganizationIdAndRecordStatus(organizationId, RecordStatus.ACTIVE.name());
    }
}
