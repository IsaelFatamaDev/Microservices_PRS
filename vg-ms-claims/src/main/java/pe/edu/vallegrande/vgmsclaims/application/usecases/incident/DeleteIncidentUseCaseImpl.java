package pe.edu.vallegrande.vgmsclaims.application.usecases.incident;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.specific.IncidentNotFoundException;
import pe.edu.vallegrande.vgmsclaims.domain.models.Incident;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.incident.IDeleteIncidentUseCase;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IIncidentRepository;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Use case implementation for soft deleting incidents
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteIncidentUseCaseImpl implements IDeleteIncidentUseCase {
    
    private final IIncidentRepository incidentRepository;
    
    @Override
    public Mono<Incident> execute(String id) {
        log.info("Deleting incident with ID: {}", id);
        
        return incidentRepository.findById(id)
                .switchIfEmpty(Mono.error(new IncidentNotFoundException(id)))
                .flatMap(incident -> {
                    incident.setRecordStatus(RecordStatus.INACTIVE);
                    incident.setUpdatedAt(Instant.now());
                    return incidentRepository.save(incident);
                })
                .doOnSuccess(deleted -> log.info("Incident deleted (soft delete): {}", deleted.getIncidentCode()))
                .doOnError(error -> log.error("Error deleting incident: {}", error.getMessage()));
    }
}
