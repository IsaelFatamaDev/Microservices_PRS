package pe.edu.vallegrande.vgmscommercial.domain.ports.in.servicecut;

import pe.edu.vallegrande.vgmscommercial.domain.models.ServiceCut;
import reactor.core.publisher.Mono;

public interface ICreateServiceCutUseCase {
    Mono<ServiceCut> execute(ServiceCut serviceCut);
}