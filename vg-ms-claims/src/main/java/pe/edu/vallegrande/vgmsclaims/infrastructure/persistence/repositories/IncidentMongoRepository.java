package pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmsclaims.infrastructure.persistence.documents.IncidentDocument;
import reactor.core.publisher.Flux;

/**
 * Reactive MongoDB repository for incidents
 */
@Repository
public interface IncidentMongoRepository extends ReactiveMongoRepository<IncidentDocument, String> {
    
    Flux<IncidentDocument> findByOrganizationId(String organizationId);
    
    Flux<IncidentDocument> findByZoneId(String zoneId);
    
    Flux<IncidentDocument> findBySeverity(String severity);
    
    Flux<IncidentDocument> findByStatus(String status);
    
    Flux<IncidentDocument> findByIncidentTypeId(String incidentTypeId);
    
    Flux<IncidentDocument> findByAssignedToUserId(String assignedToUserId);
    
    Flux<IncidentDocument> findByResolved(Boolean resolved);
    
    Flux<IncidentDocument> findByRecordStatus(String recordStatus);
    
    Flux<IncidentDocument> findByReportedByUserId(String reportedByUserId);
}
