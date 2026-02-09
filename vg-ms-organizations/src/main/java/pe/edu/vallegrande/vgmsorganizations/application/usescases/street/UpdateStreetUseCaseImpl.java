package pe.edu.vallegrande.vgmsorganizations.application.usescases.street;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific.StreetNotFoundException;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Street;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.street.IUpdateStreetUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.street.IStreetEventPublisher;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.street.IStreetRepository;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateStreetUseCaseImpl implements IUpdateStreetUseCase {

    private final IStreetRepository streetRepository;
    private final IStreetEventPublisher eventPublisher;

    @Override
    public Mono<Street> execute(String id, Street changes, String updatedBy) {
        return streetRepository.findById(id)
            .switchIfEmpty(Mono.error(new StreetNotFoundException(id)))
            .flatMap(existing -> {
                Map<String, Object> changedFields = detectChanges(existing, changes);
                if (changedFields.isEmpty()) return Mono.just(existing);

                Street updated = existing.updateWith(changes, updatedBy);
                return streetRepository.update(updated)
                    .flatMap(saved -> eventPublisher.publishStreetUpdated(saved, changedFields, updatedBy)
                        .thenReturn(saved)
                        .onErrorResume(e -> Mono.just(saved))
                    );
            });
    }

    private Map<String, Object> detectChanges(Street existing, Street changes) {
        Map<String, Object> changedFields = new HashMap<>();
        if (changes.getStreetType() != null && !changes.getStreetType().equals(existing.getStreetType())) {
            changedFields.put("streetType", changes.getStreetType().name());
        }
        if (changes.getStreetName() != null && !changes.getStreetName().equals(existing.getStreetName())) {
            changedFields.put("streetName", changes.getStreetName());
        }
        return changedFields;
    }
}

