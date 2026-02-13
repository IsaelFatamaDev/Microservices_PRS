package pe.edu.vallegrande.vgmswaterquality.domain.ports.in.testingpoint;

import pe.edu.vallegrande.vgmswaterquality.domain.models.TestingPoint;
import reactor.core.publisher.Mono;

public interface IDeleteTestingPointUseCase {
    Mono<TestingPoint> execute(String id ,String deleteBy , String reason);
}

