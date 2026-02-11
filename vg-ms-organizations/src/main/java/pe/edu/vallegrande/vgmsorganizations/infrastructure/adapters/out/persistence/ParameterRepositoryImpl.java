package pe.edu.vallegrande.vgmsorganizations.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsorganizations.application.mappers.ParameterMapper;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Parameter;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.parameter.IParameterRepository;
import pe.edu.vallegrande.vgmsorganizations.infrastructure.persistence.repositories.ParameterMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ParameterRepositoryImpl implements IParameterRepository {

    private final ParameterMongoRepository mongoRepository;
    private final ParameterMapper mapper;

    @Override
    public Mono<Parameter> save(Parameter parameter) {
        return mongoRepository.save(mapper.toDocument(parameter)).map(mapper::toModel);
    }

    @Override
    public Mono<Parameter> update(Parameter parameter) {
        return mongoRepository.save(mapper.toDocument(parameter)).map(mapper::toModel);
    }

    @Override
    public Mono<Parameter> findById(String id) {
        return mongoRepository.findById(id).map(mapper::toModel);
    }

    @Override
    public Flux<Parameter> findAll() {
        return mongoRepository.findAll().map(mapper::toModel);
    }

    @Override
    public Flux<Parameter> findByRecordStatus(RecordStatus status) {
        return mongoRepository.findByRecordStatus(status.name()).map(mapper::toModel);
    }

    @Override
    public Flux<Parameter> findByOrganizationId(String organizationId) {
        return mongoRepository.findByOrganizationId(organizationId).map(mapper::toModel);
    }

    @Override
    public Flux<Parameter> findByOrganizationIdAndRecordStatus(String organizationId, RecordStatus status) {
        return mongoRepository.findByOrganizationIdAndRecordStatus(organizationId, status.name()).map(mapper::toModel);
    }

    @Override
    public Mono<Boolean> existsByParameterTypeAndOrganizationId(String parameterType, String organizationId) {
        return mongoRepository.existsByParameterTypeAndOrganizationId(parameterType, organizationId);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return mongoRepository.deleteById(id);
    }
}