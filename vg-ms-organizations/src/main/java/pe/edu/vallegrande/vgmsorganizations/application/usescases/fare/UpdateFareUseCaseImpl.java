package pe.edu.vallegrande.vgmsorganizations.application.usescases.fare;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific.FareNotFoundException;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Fare;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.fare.IUpdateFareUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.fare.IFareEventPublisher;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.fare.IFareRepository;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateFareUseCaseImpl implements IUpdateFareUseCase {

    private final IFareRepository fareRepository;
    private final IFareEventPublisher eventPublisher;

    @Override
    public Mono<Fare> execute(String id, Fare changes, String updatedBy) {
        return fareRepository.findById(id)
            .switchIfEmpty(Mono.error(new FareNotFoundException(id)))
            .flatMap(existing -> {
                Map<String, Object> changedFields = detectChanges(existing, changes);
                if (changedFields.isEmpty()) return Mono.just(existing);

                Fare updated = existing.updateWith(changes, updatedBy);
                return fareRepository.update(updated)
                    .flatMap(saved -> eventPublisher.publishFareUpdated(saved, changedFields, updatedBy)
                        .thenReturn(saved)
                        .onErrorResume(e -> Mono.just(saved))
                    );
            });
    }

    private Map<String, Object> detectChanges(Fare existing, Fare changes) {
        Map<String, Object> changedFields = new HashMap<>();
        if (changes.getFareType() != null && !changes.getFareType().equals(existing.getFareType())) {
            changedFields.put("fareType", changes.getFareType());
        }
        if (changes.getAmount() != null && !changes.getAmount().equals(existing.getAmount())) {
            changedFields.put("amount", changes.getAmount());
        }
        if (changes.getDescription() != null && !changes.getDescription().equals(existing.getDescription())) {
            changedFields.put("description", changes.getDescription());
        }
        return changedFields;
    }
}