package pe.edu.vallegrande.vgmsclaims.application.usecases.incidenttype;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.domain.models.IncidentType;
import pe.edu.vallegrande.vgmsclaims.domain.models.valueobjects.RecordStatus;
import pe.edu.vallegrande.vgmsclaims.domain.ports.in.incidenttype.ICreateIncidentTypeUseCase;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.IIncidentTypeRepository;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.ISecurityContext;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Use case implementation for creating incident types
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateIncidentTypeUseCaseImpl implements ICreateIncidentTypeUseCase {
    
    private final IIncidentTypeRepository incidentTypeRepository;
    private final ISecurityContext securityContext;
    
    @Override
    public Mono<IncidentType> execute(IncidentType incidentType) {
        return securityContext.getCurrentUser()
                .flatMap(user -> {
                    incidentType.setOrganizationId(user.getOrganizationId());
                    incidentType.setRecordStatus(RecordStatus.ACTIVE);
                    incidentType.setCreatedAt(Instant.now());
                    incidentType.setUpdatedAt(Instant.now());
                    
                    log.info("Creating incident type: {} for organization: {}", 
                            incidentType.getTypeCode(), incidentType.getOrganizationId());
                    
                    return incidentTypeRepository.save(incidentType);
                })
                .doOnSuccess(saved -> log.info("Incident type created with id: {}", saved.getId()));
    }
}
