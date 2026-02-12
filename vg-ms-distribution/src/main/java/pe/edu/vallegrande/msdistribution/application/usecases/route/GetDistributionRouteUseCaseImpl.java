package pe.edu.vallegrande.msdistribution.application.usecases.route;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.msdistribution.domain.exceptions.specific.DistributionRouteNotFoundException;
import pe.edu.vallegrande.msdistribution.domain.models.DistributionRoute;
import pe.edu.vallegrande.msdistribution.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.msdistribution.domain.ports.in.route.IGetDistributionRouteUseCase;
import pe.edu.vallegrande.msdistribution.domain.ports.out.route.IDistributionRouteRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GetDistributionRouteUseCaseImpl implements IGetDistributionRouteUseCase {

    private final IDistributionRouteRepository repository;

    @Override
    public Mono<DistributionRoute> findById(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new DistributionRouteNotFoundException(id)));
    }

    @Override
    public Flux<DistributionRoute> findAll() { return repository.findAll(); }

    @Override
    public Flux<DistributionRoute> findAllActive() { return repository.findByRecordStatus(RecordStatus.ACTIVE); }
}
