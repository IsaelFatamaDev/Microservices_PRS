package pe.edu.vallegrande.vgmsclaims.application.usecases.incidenttype;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.base.NotFoundException;
import pe.edu.vallegrande.vgmsclaims.domain.models.IncidentType;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.incidenttype.IRestoreIncidentTypeUseCase;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IIncidentTypeRepository;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Use case implementation for restoring incident types
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RestoreIncidentTypeUseCaseImpl implements IRestoreIncidentTypeUseCase {
    
    private final IIncidentTypeRepository incidentTypeRepository;
    
    @Override
    public Mono<IncidentType> execute(String id) {
        return incidentTypeRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("IncidentType", id)))
                .flatMap(existing -> {
                    existing.setRecordStatus(RecordStatus.ACTIVE);
                    existing.setUpdatedAt(Instant.now());
                    
                    log.info("Restoring incident type: {}", id);
                    return incidentTypeRepository.save(existing);
                })
                .doOnSuccess(restored -> log.info("Incident type restored: {}", restored.getId()));
    }
}
