package pe.edu.vallegrande.vgmsorganizations.domain.ports.in.parameter;

import pe.edu.vallegrande.vgmsorganizations.domain.models.Parameter;
import reactor.core.publisher.Mono;

public interface IRestoreParameterUseCase {
    Mono<Parameter> execute(String id, String restoredBy);
}
