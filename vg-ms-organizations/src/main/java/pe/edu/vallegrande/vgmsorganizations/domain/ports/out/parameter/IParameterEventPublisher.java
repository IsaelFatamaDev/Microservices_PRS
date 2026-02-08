package pe.edu.vallegrande.vgmsorganizations.domain.ports.out.parameter;

import pe.edu.vallegrande.vgmsorganizations.domain.models.Parameter;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface IParameterEventPublisher {
    Mono<Void> publishParameterCreated(Parameter parameter, String createdBy);

    Mono<Void> publishParameterUpdated(Parameter parameter, Map<String, Object> changedFields, String updatedBy);

    Mono<Void> publishParameterDeleted(String parameterId, String organizationId, String reason, String deletedBy);

    Mono<Void> publishParameterRestored(String parameterId, String organizationId, String restoredBy);
}