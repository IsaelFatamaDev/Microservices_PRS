package pe.edu.vallegrande.vgmsorganizations.application.usescases.fare;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.base.BusinessRuleException;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific.FareNotFoundException;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Fare;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.fare.IRestoreFareUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.fare.IFareEventPublisher;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.fare.IFareRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestoreFareUseCaseImpl implements IRestoreFareUseCase {

    private final IFareRepository fareRepository;
    private final IFareEventPublisher eventPublisher;

    @Override
    public Mono<Fare> execute(String id, String restoredBy) {
        return fareRepository.findById(id)
            .switchIfEmpty(Mono.error(new FareNotFoundException(id)))
            .flatMap(fare -> {
                if (fare.isActive()) {
                    return Mono.error(new BusinessRuleException("La tarifa ya se encuentra activa"));
                }
                Fare restored = fare.restore(restoredBy);
                return fareRepository.update(restored)
                    .flatMap(saved -> eventPublisher.publishFareRestored(saved.getId(), saved.getOrganizationId(), restoredBy)
                        .thenReturn(saved)
                        .onErrorResume(e -> Mono.just(saved))
                    );
            });
    }
}
