package pe.edu.vallegrande.vgmsorganizations.application.usescases.parameter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.base.BusinessRuleException;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific.ParameterNotFoundException;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Parameter;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.parameter.IDeleteParameterUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.parameter.IParameterEventPublisher;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.parameter.IParameterRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteParameterUseCaseImpl implements IDeleteParameterUseCase {

    private final IParameterRepository parameterRepository;
    private final IParameterEventPublisher eventPublisher;

    @Override
    public Mono<Parameter> execute(String id, String deletedBy, String reason) {
        return parameterRepository.findById(id)
            .switchIfEmpty(Mono.error(new ParameterNotFoundException(id)))
            .flatMap(param -> {
                if (param.isInactive()) {
                    return Mono.error(new BusinessRuleException("Parameter is already inactive"));
                }
                Parameter deleted = param.markAsDeleted(deletedBy);
                return parameterRepository.update(deleted)
                    .flatMap(saved -> eventPublisher.publishParameterDeleted(saved.getId(), saved.getOrganizationId(), reason, deletedBy)
                        .thenReturn(saved)
                        .onErrorResume(e -> Mono.just(saved))
                    );
            });
    }
}