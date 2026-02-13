package pe.edu.vallegrande.vgmsclaims.application.usecases.incidenttype;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.base.NotFoundException;
import pe.edu.vallegrande.vgmsclaims.domain.models.IncidentType;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.incidenttype.IUpdateIncidentTypeUseCase;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IIncidentTypeRepository;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Use case implementation for updating incident types
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateIncidentTypeUseCaseImpl implements IUpdateIncidentTypeUseCase {
    
    private final IIncidentTypeRepository incidentTypeRepository;
    
    @Override
    public Mono<IncidentType> execute(String id, IncidentType incidentType) {
        return incidentTypeRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("IncidentType", id)))
                .flatMap(existing -> {
                    if (incidentType.getTypeName() != null) {
                        existing.setTypeName(incidentType.getTypeName());
                    }
                    if (incidentType.getDescription() != null) {
                        existing.setDescription(incidentType.getDescription());
                    }
                    if (incidentType.getPriorityLevel() != null) {
                        existing.setPriorityLevel(incidentType.getPriorityLevel());
                    }
                    if (incidentType.getEstimatedResolutionTime() != null) {
                        existing.setEstimatedResolutionTime(incidentType.getEstimatedResolutionTime());
                    }
                    if (incidentType.getRequiresExternalService() != null) {
                        existing.setRequiresExternalService(incidentType.getRequiresExternalService());
                    }
                    existing.setUpdatedAt(Instant.now());
                    
                    log.info("Updating incident type: {}", id);
                    return incidentTypeRepository.save(existing);
                })
                .doOnSuccess(updated -> log.info("Incident type updated: {}", updated.getId()));
    }
}
