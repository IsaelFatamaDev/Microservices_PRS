package pe.edu.vallegrande.vgmswaterquality.domain.ports.out;

import pe.edu.vallegrande.vgmswaterquality.domain.models.QualityTest;
import pe.edu.vallegrande.vgmswaterquality.domain.models.TestingPoint;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface IWaterQualityEventPublisher {
    Mono<Void>publishTestCreated(QualityTest test , String createdBy);

    Mono<Void> publishTestUpdated(QualityTest test , Map<String, Object> changedFields, String updatedBy);

    Mono<Void> publishTestDeleted(String testId , String organizationId ,String deletedBy , String reason);

    Mono<Void> publishTestRestore(String testId , String organizationId , String restoredBy);

    // Events Testing Points
    Mono<Void> publishTestingCreated(TestingPoint point , String createdBy);

    Mono<Void> publishTestingUpdated( TestingPoint point , Map<String , Object> changedFields, String updatedBy);

    Mono<Void> publishTestingDeleted(String pointId, TestingPoint point , String deletedBy);

    Mono<Void> publishTestingRestored(String pointId , String organizationId , String restoredBy);
}
