package pe.edu.vallegrande.msdistribution.application.usecases.route;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.msdistribution.domain.exceptions.specific.DistributionRouteNotFoundException;
import pe.edu.vallegrande.msdistribution.domain.ports.in.route.IDeleteDistributionRouteUseCase;
import pe.edu.vallegrande.msdistribution.domain.ports.out.route.IDistributionRouteEventPublisher;
import pe.edu.vallegrande.msdistribution.domain.ports.out.route.IDistributionRouteRepository;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DeleteDistributionRouteUseCaseImpl implements IDeleteDistributionRouteUseCase {
    private final IDistributionRouteRepository repository;
    private final IDistributionRouteEventPublisher eventPublisher;

    @Override
    public Mono<Void> execute(String id, String deletedBy, String reason) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new DistributionRouteNotFoundException(id)))
                .flatMap(existing -> repository.update(existing.markAsDeleted(deletedBy)))
                .flatMap(saved -> eventPublisher.publishRouteDeleted(id, deletedBy, reason))
                .then();
    }
}
