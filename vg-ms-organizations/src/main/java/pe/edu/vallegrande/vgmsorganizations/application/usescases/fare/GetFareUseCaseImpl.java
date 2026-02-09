package pe.edu.vallegrande.vgmsorganizations.application.usescases.fare;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific.FareNotFoundException;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Fare;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.fare.IGetFareUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.fare.IFareRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetFareUseCaseImpl implements IGetFareUseCase {

    private final IFareRepository fareRepository;

    @Override
    public Mono<Fare> findById(String id) {
        return fareRepository.findById(id)
            .switchIfEmpty(Mono.error(new FareNotFoundException(id)));
    }

    @Override
    public Flux<Fare> findAllActive() {
        return fareRepository.findByRecordStatus(RecordStatus.ACTIVE);
    }

    @Override
    public Flux<Fare> findAll() {
        return fareRepository.findAll();
    }

    @Override
    public Flux<Fare> findByOrganizationId(String organizationId) {
        return fareRepository.findByOrganizationId(organizationId);
    }

    @Override
    public Flux<Fare> findActiveByOrganizationId(String organizationId) {
        return fareRepository.findByOrganizationIdAndRecordStatus(organizationId, RecordStatus.ACTIVE);
    }
}