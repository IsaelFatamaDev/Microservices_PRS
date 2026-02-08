package pe.edu.vallegrande.vgmsorganizations.domain.ports.in.parameter;

import pe.edu.vallegrande.vgmsorganizations.domain.models.Parameter;
import reactor.core.publisher.Mono;

public interface ICreateParameterUseCase {
    Mono<Parameter> execute(Parameter parameter, String createdBy);
}
