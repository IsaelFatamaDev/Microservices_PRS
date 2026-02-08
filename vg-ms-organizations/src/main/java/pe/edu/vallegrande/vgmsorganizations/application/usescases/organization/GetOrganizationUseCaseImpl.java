package pe.edu.vallegrande.vgmsorganizations.application.usescases.organization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsorganizations.domain.exceptions.specific.OrganizationNotFoundException;
import pe.edu.vallegrande.vgmsorganizations.domain.models.Organization;
import pe.edu.vallegrande.vgmsorganizations.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.in.organization.IGetOrganizationUseCase;
import pe.edu.vallegrande.vgmsorganizations.domain.ports.out.organization.IOrganizationRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetOrganizationUseCaseImpl implements IGetOrganizationUseCase {

    private final IOrganizationRepository organizationRepository;

    @Override
    public Mono<Organization> findById(String id) {
        log.info("Finding organization by id: {}", id);
        return organizationRepository.findById(id)
            .switchIfEmpty(Mono.error(new OrganizationNotFoundException("Organization not found")));
    }

    @Override
    public Flux<Organization> findAll() {
        log.info("Finding all organizations");
        return organizationRepository.findAll();
    }

    @Override
    public Flux<Organization> findAllActive() {
        log.info("Finding all active organizations");
        return organizationRepository.findByRecordStatus(RecordStatus.ACTIVE.name());
    }
}
