package pe.edu.vallegrande.vgmsorganizations.application.usescases.parameter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific.ParameterNotFoundException;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Parameter;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.parameter.IGetParameterUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.parameter.IParameterRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetParameterUseCaseImpl implements IGetParameterUseCase {

    private final IParameterRepository parameterRepository;

    @Override
    public Mono<Parameter> findById(String id) {
        return parameterRepository.findById(id)
            .switchIfEmpty(Mono.error(new ParameterNotFoundException(id)));
    }

    @Override
    public Flux<Parameter> findAllActive() {
        return parameterRepository.findByRecordStatus(RecordStatus.ACTIVE);
    }

    @Override
    public Flux<Parameter> findAll() {
        return parameterRepository.findAll();
    }

    @Override
    public Flux<Parameter> findByOrganizationId(String organizationId) {
        return parameterRepository.findByOrganizationId(organizationId);
    }

    @Override
    public Flux<Parameter> findActiveByOrganizationId(String organizationId) {
        return parameterRepository.findByOrganizationIdAndRecordStatus(organizationId, RecordStatus.ACTIVE);
    }
}