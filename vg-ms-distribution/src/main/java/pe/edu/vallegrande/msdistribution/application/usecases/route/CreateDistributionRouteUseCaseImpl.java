package pe.edu.vallegrande.msdistribution.application.usecases.route;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.msdistribution.domain.models.DistributionRoute;
import pe.edu.vallegrande.msdistribution.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.msdistribution.domain.ports.in.route.ICreateDistributionRouteUseCase;
import pe.edu.vallegrande.msdistribution.domain.ports.out.route.IDistributionRouteEventPublisher;
import pe.edu.vallegrande.msdistribution.domain.ports.out.route.IDistributionRouteRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateDistributionRouteUseCaseImpl implements ICreateDistributionRouteUseCase {

    private final IDistributionRouteRepository repository;
    private final IDistributionRouteEventPublisher eventPublisher;

    @Override
    public Mono<DistributionRoute> execute(DistributionRoute route, String createdBy) {
        LocalDateTime now = LocalDateTime.now();
        DistributionRoute newRoute = route.toBuilder()
                .recordStatus(RecordStatus.ACTIVE)
                .createdAt(now)
                .createdBy(createdBy)
                .updatedAt(now)
                .updatedBy(createdBy)
                .build();
        return repository.save(newRoute)
                .flatMap(saved -> eventPublisher.publishRouteCreated(saved, createdBy)
                        .thenReturn(saved));
    }
}
