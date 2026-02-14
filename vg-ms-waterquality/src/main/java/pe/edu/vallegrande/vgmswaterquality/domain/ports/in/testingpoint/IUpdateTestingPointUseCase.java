package pe.edu.vallegrande.vgmswaterquality.domain.ports.in.testingpoint;

import pe.edu.vallegrande.vgmswaterquality.domain.models.TestingPoint;
import reactor.core.publisher.Mono;

public interface IUpdateTestingPointUseCase {
    Mono<TestingPoint> execute(String id , TestingPoint point , String udatedBy);
}
