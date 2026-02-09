package pe.edu.vallegrande.vgmsorganizations.application.usescases.street;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.base.BusinessRuleException;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific.StreetNotFoundException;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Street;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.street.IRestoreStreetUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.street.IStreetEventPublisher;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.street.IStreetRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestoreStreetUseCaseImpl implements IRestoreStreetUseCase {

    private final IStreetRepository streetRepository;
    private final IStreetEventPublisher eventPublisher;

    @Override
    public Mono<Street> execute(String id, String restoredBy) {
        return streetRepository.findById(id)
            .switchIfEmpty(Mono.error(new StreetNotFoundException(id)))
            .flatMap(street -> {
                if (street.isActive()) {
                    return Mono.error(new BusinessRuleException("La calle ya se encuentra activa"));
                }
                Street restored = street.restore(restoredBy);
                return streetRepository.update(restored)
                    .flatMap(saved -> eventPublisher.publishStreetRestored(saved.getId(), saved.getZoneId(), restoredBy)
                        .thenReturn(saved)
                        .onErrorResume(e -> Mono.just(saved))
                    );
            });
    }
}