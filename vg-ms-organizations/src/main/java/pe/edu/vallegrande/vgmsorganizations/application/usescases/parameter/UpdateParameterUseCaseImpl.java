package pe.edu.vallegrande.vgmsorganizations.application.usescases.parameter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific.ParameterNotFoundException;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Parameter;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.parameter.IUpdateParameterUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.parameter.IParameterEventPublisher;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.parameter.IParameterRepository;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateParameterUseCaseImpl implements IUpdateParameterUseCase {

    private final IParameterRepository parameterRepository;
    private final IParameterEventPublisher eventPublisher;

    @Override
    public Mono<Parameter> execute(String id, Parameter changes, String updatedBy) {
        return parameterRepository.findById(id)
            .switchIfEmpty(Mono.error(new ParameterNotFoundException(id)))
            .flatMap(existing -> {
                Map<String, Object> changedFields = detectChanges(existing, changes);
                if (changedFields.isEmpty()) return Mono.just(existing);

                Parameter updated = existing.updateWith(changes, updatedBy);
                return parameterRepository.update(updated)
                    .flatMap(saved -> eventPublisher.publishParameterUpdated(saved, changedFields, updatedBy)
                        .thenReturn(saved)
                        .onErrorResume(e -> Mono.just(saved))
                    );
            });
    }

    private Map<String, Object> detectChanges(Parameter existing, Parameter changes) {
        Map<String, Object> changedFields = new HashMap<>();
        if (changes.getParameterValue() != null && !changes.getParameterValue().equals(existing.getParameterValue())) {
            changedFields.put("parameterValue", changes.getParameterValue());
        }
        if (changes.getDescription() != null && !changes.getDescription().equals(existing.getDescription())) {
            changedFields.put("description", changes.getDescription());
        }
        return changedFields;
    }
}
