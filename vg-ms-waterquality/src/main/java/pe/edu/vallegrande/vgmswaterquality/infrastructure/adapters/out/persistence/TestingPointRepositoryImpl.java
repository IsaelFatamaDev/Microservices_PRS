package pe.edu.vallegrande.vgmswaterquality.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmswaterquality.application.mappers.TestingPointMapper;
import pe.edu.vallegrande.vgmswaterquality.domain.models.TestingPoint;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.out.ITestingPointRepository;
import pe.edu.vallegrande.vgmswaterquality.infrastructure.persistence.repositories.TestingPointMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TestingPointRepositoryImpl implements ITestingPointRepository {

    private final TestingPointMongoRepository repository;
    private final TestingPointMapper mapper;

    @Override
    public Mono<TestingPoint> save(TestingPoint point) {
        return repository.save(mapper.toDocument(point)).map(mapper::toDomain);
    }

    @Override
    public Mono<TestingPoint> update(TestingPoint update) {
        return repository.save(mapper.toDocument(update)).map(mapper::toDomain);
    }

    @Override
    public Mono<TestingPoint> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Flux<TestingPoint> findByOrganizationId(String organizationId) {
        return repository.findByOrganizationId(organizationId).map(mapper::toDomain);
    }

    @Override
    public Flux<TestingPoint> findAll() {
        return repository.findAll().map(mapper::toDomain);
    }

    @Override
    public Flux<TestingPoint> findByRecordStatus(String recordStatus) {
        return repository.findByRecordStatus(recordStatus).map(mapper::toDomain);
    }

    @Override
    public Flux<TestingPoint> findByOrganizationIdAndRecordStatus(String organizationId, String recordStatus) {
        return repository.findByOrganizationIdAndRecordStatus(organizationId, recordStatus).map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }
}
