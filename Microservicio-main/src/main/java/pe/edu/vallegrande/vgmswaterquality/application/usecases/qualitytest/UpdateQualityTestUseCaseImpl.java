package pe.edu.vallegrande.vgmswaterquality.application.usecases.qualitytest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmswaterquality.domain.exceptions.QualityTestNotFoundException;
import pe.edu.vallegrande.vgmswaterquality.domain.models.QualityTest;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.in.qualitytest.IUpdateQualityTestUseCase;
import pe.edu.vallegrande.vgmswaterquality.domain.ports.out.IQualityTestRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateQualityTestUseCaseImpl implements IUpdateQualityTestUseCase {

    private final IQualityTestRepository repository;

    @Override
    public Mono<QualityTest> execute(String id, QualityTest test, String updatedBy) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new QualityTestNotFoundException(id)))
                .flatMap(existingTest -> {
                    existingTest.setOrganizationId(test.getOrganizationId());
                    existingTest.setTestingPointId(test.getTestingPointId());
                    existingTest.setTestDate(test.getTestDate());
                    existingTest.setTestType(test.getTestType());
                    existingTest.setChlorineLevel(test.getChlorineLevel());
                    existingTest.setPhLevel(test.getPhLevel());
                    existingTest.setTurbidityLevel(test.getTurbidityLevel());
                    existingTest.setTestResult(test.getTestResult());
                    existingTest.setTestedByUserId(test.getTestedByUserId());
                    existingTest.setObservations(test.getObservations());
                    existingTest.setTreatmentApplied(test.getTreatmentApplied());
                    existingTest.setTreatmentDescription(test.getTreatmentDescription());
                    existingTest.setUpdatedAt(LocalDateTime.now());
                    existingTest.setUpdatedBy(updatedBy);

                    return repository.update(existingTest);
                })
                .doOnSuccess(t -> log.info("QualityTest updated: {}", t.getId()))
                .doOnError(e -> log.error("Error updating QualityTest: {}", e.getMessage()));
    }
}
