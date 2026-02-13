package pe.edu.vallegrande.vgmscommercial.domain.ports.out;

import reactor.core.publisher.Mono;

public interface IInfrastructureClient {
    Mono<Boolean> existsWaterBox(String waterBoxId);
    Mono<String> getWaterBoxByUserId(String userId);
    Mono<Void> updateWaterBoxStatus(String waterBoxId, String status);
}