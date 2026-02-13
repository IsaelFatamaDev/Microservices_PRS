package pe.edu.vallegrande.vgmscommercial.domain.ports.in.pettycash;

import pe.edu.vallegrande.vgmscommercial.domain.models.PettyCash;
import reactor.core.publisher.Mono;

public interface IUpdatePettyCashUseCase {
    Mono<PettyCash> execute(String id, PettyCash pettyCash);
}