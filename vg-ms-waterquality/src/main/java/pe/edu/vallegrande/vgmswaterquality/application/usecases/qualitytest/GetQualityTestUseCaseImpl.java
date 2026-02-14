package pe.edu.vallegrande.vgmswaterquality.application.usecases.qualitytest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmswaterquality.domain.exceptions.QualityTestNotFoundException;
import pe.edu.vallegrande.vgmswaterquality.domain.models.QualityTest;
import pe.edu.vallegrande.vgmswaterquality.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.in.qualitytest.IGetQualityTestUseCase;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.out.IQualityTestRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetQualityTestUseCaseImpl implements IGetQualityTestUseCase {

    private final IQualityTestRepository repository;

    @Override
    public Mono<QualityTest> findByAll(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new QualityTestNotFoundException(id)));
    }

    @Override
    public Flux<QualityTest> findAll() {
        return repository.findAll();
    }

    @Override
    public Flux<QualityTest> findAllActive() {
        return repository.findByRecordStatus(RecordStatus.ACTIVE.name());
    }

    @Override
    public Flux<QualityTest> findByOrganizationId(String organizationId) {
        return repository.findByOrganizationId(organizationId);
    }

    @Override
    public Flux<QualityTest> findActiveByOrganizationId(String organizationId) {
        return repository.findByOrganizationIdAndRecordStatus(organizationId, RecordStatus.ACTIVE);
    }
}
