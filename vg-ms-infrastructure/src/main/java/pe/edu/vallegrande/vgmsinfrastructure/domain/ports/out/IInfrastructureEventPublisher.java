package pe.edu.vallegrande.vgmsinfrastructure.domain.ports.out;

import reactor.core.publisher.Mono;

public interface IInfrastructureEventPublisher {
    Mono<Void> publishWaterBoxCreated(String waterBoxId, String boxCode, String createdBy);
    Mono<Void> publishWaterBoxUpdated(String waterBoxId, String updatedBy);
    Mono<Void> publishWaterBoxDeleted(String waterBoxId, String deletedBy);
    Mono<Void> publishWaterBoxRestored(String waterBoxId, String restoredBy);
    Mono<Void> publishWaterBoxAssigned(String waterBoxId, String userId, String assignedBy);
    Mono<Void> publishWaterBoxTransferred(String waterBoxId, String fromUserId, String toUserId, String transferredBy);
    Mono<Void> publishWaterBoxSuspended(String waterBoxId, String suspendedBy);
    Mono<Void> publishWaterBoxReconnected(String waterBoxId, String reconnectedBy);
}
