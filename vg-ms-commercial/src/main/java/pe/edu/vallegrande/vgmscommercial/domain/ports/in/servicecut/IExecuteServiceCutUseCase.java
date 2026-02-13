package pe.edu.vallegrande.vgmscommercial.domain.ports.in.servicecut;

import pe.edu.vallegrande.vgmscommercial.domain.models.ServiceCut;
import reactor.core.publisher.Mono;

public interface IExecuteServiceCutUseCase {
    Mono<ServiceCut> execute(String id, String organizationId);
}