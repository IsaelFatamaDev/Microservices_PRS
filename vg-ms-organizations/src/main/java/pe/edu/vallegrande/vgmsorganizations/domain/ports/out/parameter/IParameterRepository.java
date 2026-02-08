package pe.edu.vallegrande.vgmsorganizations.domain.ports.out.parameter;

import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Parameter;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface IParameterRepository {
    Mono<Parameter> save(Parameter parameter);

    Mono<Parameter> update(Parameter parameter);

    Mono<Parameter> findById(String id);

    Flux<Parameter> findAll();

    Flux<Parameter> findByRecordStatus(RecordStatus status);

    Flux<Parameter> findByOrganizationId(String organizationId);

    Flux<Parameter> findByOrganizationIdAndRecordStatus(String organizationId, RecordStatus status);

    Mono<Boolean> existsByParameterTypeAndOrganizationId(String parameterType, String organizationId);

    Mono<Void> deleteById(String id);
}