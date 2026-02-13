package pe.edu.vallegrande.vgmsclaims.application.usecases.incident;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.specific.IncidentNotFoundException;
import pe.edu.vallegrande.vgmsclaims.domain.models.Incident;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.incident.IRestoreIncidentUseCase;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IIncidentRepository;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Use case implementation for restoring deleted incidents
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RestoreIncidentUseCaseImpl implements IRestoreIncidentUseCase {
    
    private final IIncidentRepository incidentRepository;
    
    @Override
    public Mono<Incident> execute(String id) {
        log.info("Restoring incident with ID: {}", id);
        
        return incidentRepository.findById(id)
                .switchIfEmpty(Mono.error(new IncidentNotFoundException(id)))
                .flatMap(incident -> {
                    incident.setRecordStatus(RecordStatus.ACTIVE);
                    incident.setUpdatedAt(Instant.now());
                    return incidentRepository.save(incident);
                })
                .doOnSuccess(restored -> log.info("Incident restored: {}", restored.getIncidentCode()))
                .doOnError(error -> log.error("Error restoring incident: {}", error.getMessage()));
    }
}
