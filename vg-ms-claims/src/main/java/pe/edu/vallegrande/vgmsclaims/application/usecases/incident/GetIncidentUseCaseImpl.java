package pe.edu.vallegrande.vgmsclaims.application.usecases.incident;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.specific.IncidentNotFoundException;
import pe.edu.vallegrande.vgmsclaims.domain.models.Incident;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.incident.IGetIncidentUseCase;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IIncidentRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Use case implementation for getting incidents
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetIncidentUseCaseImpl implements IGetIncidentUseCase {

    private final IIncidentRepository incidentRepository;

    @Override
    public Flux<Incident> findAll() {
        log.info("Getting all incidents");
        return incidentRepository.findAll()
                .doOnComplete(() -> log.info("Consulta de incidents completada"));
    }

    @Override
    public Mono<Incident> findById(String id) {
        log.info("Finding incident with ID: {}", id);
        return incidentRepository.findById(id)
                .switchIfEmpty(Mono.error(new IncidentNotFoundException(id)))
                .doOnSuccess(incident -> log.info("Incident found: {}", incident.getIncidentCode()));
    }

    @Override
    public Flux<Incident> findByOrganizationId(String organizationId) {
        log.info("Finding incidents de organization: {}", organizationId);
        return incidentRepository.findByOrganizationId(organizationId);
    }

    @Override
    public Flux<Incident> findByZoneId(String zoneId) {
        log.info("Finding incidents de zone: {}", zoneId);
        return incidentRepository.findByZoneId(zoneId);
    }

    @Override
    public Flux<Incident> findBySeverity(String severity) {
        log.info("Finding incidents with severidad: {}", severity);
        return incidentRepository.findBySeverity(severity);
    }

    @Override
    public Flux<Incident> findByStatus(String status) {
        log.info("Finding incidents with estado: {}", status);
        return incidentRepository.findByStatus(status);
    }
}
