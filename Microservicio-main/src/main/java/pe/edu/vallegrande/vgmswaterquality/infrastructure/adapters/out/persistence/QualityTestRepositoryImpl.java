package pe.edu.vallegrande.vgmswaterquality.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmswaterquality.application.mappers.QualityTestMapper;
import pe.edu.vallegrande.vgmswaterquality.domain.models.QualityTest;
import pe.edu.vallegrande.vgmswaterquality.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.out.IQualityTestRepository;
import pe.edu.vallegrande.vgmswaterquality.infrastructure.persistence.repositories.QualityTestMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class QualityTestRepositoryImpl implements IQualityTestRepository {

    private final QualityTestMongoRepository testRepository;
    private final QualityTestMapper testMapper;

    @Override
    public Mono<QualityTest> save(QualityTest test) {
        return testRepository.save(testMapper.toDocument(test)).map(testMapper::toDomain);
    }

    @Override
    public Mono<QualityTest> update(QualityTest test) {
        return testRepository.save(testMapper.toDocument(test)).map(testMapper::toDomain);
    }

    @Override
    public Mono<QualityTest> findById(String id) {
        return testRepository.findById(id).map(testMapper::toDomain);
    }

    @Override
    public Flux<QualityTest> findByOrganizationId(String organizationId) {
        return testRepository.findByOrganizationId(organizationId).map(testMapper::toDomain);
    }

    @Override
    public Flux<QualityTest> findAll() {
        return testRepository.findAll().map(testMapper::toDomain);
    }

    @Override
    public Flux<QualityTest> findByRecordStatus(String recordStatus) {
        return testRepository.findByRecordStatus(recordStatus).map(testMapper::toDomain);
    }

    @Override
    public Flux<QualityTest> findByOrganizationIdAndRecordStatus(String organizationId, RecordStatus recordStatus) {
        return testRepository.findByOrganizationIdAndRecordStatus(organizationId, recordStatus.name())
                .map(testMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return testRepository.deleteById(id);
    }
}
