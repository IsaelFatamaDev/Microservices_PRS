package pe.edu.vallegrande.vgmswaterquality.domain.ports.in.qualitytest;

import pe.edu.vallegrande.vgmswaterquality.domain.models.QualityTest;
import reactor.core.publisher.Mono;

public interface IRestoreQualityTestUseCase {
     Mono<QualityTest> execute(String id, String restoredBy);
}
