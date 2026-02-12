package pe.edu.vallegrande.msdistribution.application.usecases.route;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.msdistribution.domain.exceptions.specific.DistributionRouteNotFoundException;
import pe.edu.vallegrande.msdistribution.domain.models.DistributionRoute;
import pe.edu.vallegrande.msdistribution.domain.ports.in.route.IUpdateDistributionRouteUseCase;
import pe.edu.vallegrande.msdistribution.domain.ports.out.route.IDistributionRouteEventPublisher;
import pe.edu.vallegrande.msdistribution.domain.ports.out.route.IDistributionRouteRepository;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class UpdateDistributionRouteUseCaseImpl implements IUpdateDistributionRouteUseCase {

    private final IDistributionRouteRepository repository;
    private final IDistributionRouteEventPublisher eventPublisher;

    @Override
    public Mono<DistributionRoute> execute(String id, DistributionRoute changes, String updatedBy) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new DistributionRouteNotFoundException(id)))
                .flatMap(existing -> {
                    DistributionRoute updated = existing.updateWith(changes, updatedBy);
                    return repository.update(updated)
                            .flatMap(saved -> eventPublisher.publishRouteUpdated(saved, new HashMap<>(), updatedBy)
                                    .thenReturn(saved));
                });
    }
}
