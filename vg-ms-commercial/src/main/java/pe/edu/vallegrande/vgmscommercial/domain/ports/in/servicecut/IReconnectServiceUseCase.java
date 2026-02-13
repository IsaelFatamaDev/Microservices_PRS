package pe.edu.vallegrande.vgmscommercial.domain.ports.in.servicecut;

import pe.edu.vallegrande.vgmscommercial.domain.models.ServiceCut;
import reactor.core.publisher.Mono;

public interface IReconnectServiceUseCase {
    Mono<ServiceCut> execute(String id, String organizationId);
}