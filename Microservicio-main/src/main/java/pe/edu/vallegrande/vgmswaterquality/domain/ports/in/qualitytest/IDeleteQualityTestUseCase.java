package pe.edu.vallegrande.vgmswaterquality.domain.ports.in.qualitytest;

import reactor.core.publisher.Mono;

public interface IDeleteQualityTestUseCase {
     Mono<Void> execute(String id, String deletedBy);
}
