package pe.edu.vallegrande.msdistribution.application.usecases.route;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.msdistribution.domain.exceptions.specific.DistributionRouteNotFoundException;
import pe.edu.vallegrande.msdistribution.domain.ports.in.route.IRestoreDistributionRouteUseCase;
import pe.edu.vallegrande.msdistribution.domain.ports.out.route.IDistributionRouteEventPublisher;
import pe.edu.vallegrande.msdistribution.domain.ports.out.route.IDistributionRouteRepository;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestoreDistributionRouteUseCaseImpl implements IRestoreDistributionRouteUseCase {
    private final IDistributionRouteRepository repository;
    private final IDistributionRouteEventPublisher eventPublisher;

    @Override
    public Mono<Void> execute(String id, String restoredBy) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new DistributionRouteNotFoundException(id)))
                .flatMap(existing -> repository.update(existing.restore(restoredBy)))
                .flatMap(saved -> eventPublisher.publishRouteRestored(id, restoredBy))
                .then();
    }
}
