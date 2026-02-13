package pe.edu.vallegrande.vgmscommercial.domain.ports.in.pettycash;

import pe.edu.vallegrande.vgmscommercial.domain.models.PettyCash;
import reactor.core.publisher.Mono;

public interface IRestorePettyCashUseCase {
    Mono<PettyCash> execute(String id, String organizationId);
}