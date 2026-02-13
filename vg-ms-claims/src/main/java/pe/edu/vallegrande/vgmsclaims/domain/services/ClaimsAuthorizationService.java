package pe.edu.vallegrande.vgmsclaims.domain.services;

import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vgmsclaims.domain.exceptions.base.BusinessRuleException;
import pe.edu.vallegrande.vgmsclaims.domain.models.Complaint;
import pe.edu.vallegrande.vgmsclaims.domain.models.Incident;
import pe.edu.vallegrande.vgmsclaims.domain.ports.out.ISecurityContext;
import reactor.core.publisher.Mono;

/**
 * Domain service for claims authorization rules
 */
@Service
public class ClaimsAuthorizationService {
    
    private final ISecurityContext securityContext;
    
    public ClaimsAuthorizationService(ISecurityContext securityContext) {
        this.securityContext = securityContext;
    }
    
    /**
     * Checks if current user can view a complaint
     */
    public Mono<Boolean> canViewComplaint(Complaint complaint) {
        return securityContext.isAdmin()
                .flatMap(isAdmin -> {
                    if (isAdmin) {
                        return Mono.just(true);
                    }
                    return securityContext.getCurrentUserId()
                            .map(userId -> userId.equals(complaint.getUserId()) ||
                                          userId.equals(complaint.getAssignedToUserId()));
                });
    }
    
    /**
     * Checks if current user can modify a complaint
     */
    public Mono<Boolean> canModifyComplaint(Complaint complaint) {
        return securityContext.isAdmin()
                .flatMap(isAdmin -> {
                    if (isAdmin) {
                        return Mono.just(true);
                    }
                    return securityContext.getCurrentUserId()
                            .map(userId -> userId.equals(complaint.getAssignedToUserId()));
                });
    }
    
    /**
     * Checks if current user can close a complaint
     */
    public Mono<Boolean> canCloseComplaint(Complaint complaint) {
        return canModifyComplaint(complaint);
    }
    
    /**
     * Checks if current user can view an incident
     */
    public Mono<Boolean> canViewIncident(Incident incident) {
        return securityContext.isAdmin()
                .flatMap(isAdmin -> {
                    if (isAdmin) {
                        return Mono.just(true);
                    }
                    return securityContext.getCurrentUserId()
                            .map(userId -> userId.equals(incident.getReportedByUserId()) ||
                                          userId.equals(incident.getAssignedToUserId()));
                });
    }
    
    /**
     * Checks if current user can modify an incident
     */
    public Mono<Boolean> canModifyIncident(Incident incident) {
        return securityContext.isAdmin()
                .flatMap(isAdmin -> {
                    if (isAdmin) {
                        return Mono.just(true);
                    }
                    return securityContext.getCurrentUserId()
                            .map(userId -> userId.equals(incident.getAssignedToUserId()));
                });
    }
    
    /**
     * Checks if current user can assign incidents
     */
    public Mono<Boolean> canAssignIncident() {
        return securityContext.isAdmin();
    }
    
    /**
     * Checks if current user can resolve an incident
     */
    public Mono<Boolean> canResolveIncident(Incident incident) {
        return securityContext.isAdmin()
                .flatMap(isAdmin -> {
                    if (isAdmin) {
                        return Mono.just(true);
                    }
                    return securityContext.getCurrentUserId()
                            .map(userId -> userId.equals(incident.getAssignedToUserId()));
                });
    }
    
    /**
     * Checks if current user can close an incident
     */
    public Mono<Boolean> canCloseIncident(Incident incident) {
        return canModifyIncident(incident);
    }
    
    /**
     * Verifies authorization and throws exception if not authorized
     */
    public Mono<Void> requireComplaintViewAccess(Complaint complaint) {
        return canViewComplaint(complaint)
                .flatMap(allowed -> {
                    if (!allowed) {
                        return Mono.error(new BusinessRuleException("ACCESS_DENIED", 
                                "You do not have permission to view this complaint"));
                    }
                    return Mono.empty();
                });
    }
    
    /**
     * Verifies authorization and throws exception if not authorized
     */
    public Mono<Void> requireIncidentViewAccess(Incident incident) {
        return canViewIncident(incident)
                .flatMap(allowed -> {
                    if (!allowed) {
                        return Mono.error(new BusinessRuleException("ACCESS_DENIED", 
                                "You do not have permission to view this incident"));
                    }
                    return Mono.empty();
                });
    }
}
