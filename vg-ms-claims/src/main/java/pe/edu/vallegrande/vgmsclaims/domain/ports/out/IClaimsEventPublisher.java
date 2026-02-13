package pe.edu.vallegrande.vgmsclaims.domain.ports.out;

/**
 * Output port for the claims event publisher
 */
public interface IClaimsEventPublisher {
    
    /**
     * Publishes a complaint created event
     * @param event event to publish
     */
    void publishComplaintCreated(Object event);
    
    /**
     * Publishes a complaint updated event
     * @param event event to publish
     */
    void publishComplaintUpdated(Object event);
    
    /**
     * Publishes a response added to complaint event
     * @param event event to publish
     */
    void publishComplaintResponseAdded(Object event);
    
    /**
     * Publishes a complaint closed event
     * @param event event to publish
     */
    void publishComplaintClosed(Object event);
    
    /**
     * Publishes an incident created event
     * @param event event to publish
     */
    void publishIncidentCreated(Object event);
    
    /**
     * Publishes an incident assigned event
     * @param event event to publish
     */
    void publishIncidentAssigned(Object event);
    
    /**
     * Publishes an incident updated event
     * @param event event to publish
     */
    void publishIncidentUpdated(Object event);
    
    /**
     * Publishes an incident resolved event
     * @param event event to publish
     */
    void publishIncidentResolved(Object event);
    
    /**
     * Publishes an incident closed event
     * @param event event to publish
     */
    void publishIncidentClosed(Object event);
    
    /**
     * Publishes an urgent incident alert
     * @param event event to publish
     */
    void publishUrgentIncidentAlert(Object event);
}
