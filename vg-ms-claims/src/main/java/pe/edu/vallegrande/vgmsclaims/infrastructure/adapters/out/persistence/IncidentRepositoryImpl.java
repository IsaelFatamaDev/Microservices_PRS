package pe.edu.vallegrande.vgmsclaims.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.vgmsclaims.application.mappers.IncidentMapper;
import pe.edu.vallegrande.vgmsclaims.domain.models.Incident;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IIncidentRepository;
import pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.repositories.IncidentMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Incident repository port implementation using MongoDB
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IncidentRepositoryImpl implements IIncidentRepository {

    private final IncidentMongoRepository incidentRepository;
    private final IncidentMapper mapper;

    @Override
    public Mono<Incident> save(Incident incident) {
        log.debug("Saving incident: {}", incident.getId());
        return incidentRepository.save(mapper.toDocument(incident))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Incident> findById(String id) {
        log.debug("Finding incident by ID: {}", id);
        return incidentRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Incident> findAll() {
        log.debug("Finding all the incidents");
        return incidentRepository.findAll()
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Incident> findByOrganizationId(String organizationId) {
        log.debug("Finding incidents by organization: {}", organizationId);
        return incidentRepository.findByOrganizationId(organizationId)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Incident> findByZoneId(String zoneId) {
        log.debug("Finding incidents by zone: {}", zoneId);
        return incidentRepository.findByZoneId(zoneId)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Incident> findBySeverity(String severity) {
        log.debug("Finding incidents by severity: {}", severity);
        return incidentRepository.findBySeverity(severity)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Incident> findByStatus(String status) {
        log.debug("Finding incidents by status: {}", status);
        return incidentRepository.findByStatus(status)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Incident> findByIncidentTypeId(String incidentTypeId) {
        log.debug("Finding incidents by type: {}", incidentTypeId);
        return incidentRepository.findByIncidentTypeId(incidentTypeId)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Incident> findByAssignedToUserId(String assignedToUserId) {
        log.debug("Searching incidents assigned to: {}", assignedToUserId);
        return incidentRepository.findByAssignedToUserId(assignedToUserId)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Incident> findByResolved(Boolean resolved) {
        log.debug("Finding incidents by resolution status: {}", resolved);
        return incidentRepository.findByResolved(resolved)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Incident> findByRecordStatus(String recordStatus) {
        log.debug("Finding incidents by record status: {}", recordStatus);
        return incidentRepository.findByRecordStatus(recordStatus)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        log.debug("Deleting incident: {}", id);
        return incidentRepository.deleteById(id);
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return incidentRepository.existsById(id);
    }
}
