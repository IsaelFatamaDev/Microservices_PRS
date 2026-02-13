package pe.edu.vallegrande.vgmscommercial.domain.ports.in.debt;

import pe.edu.vallegrande.vgmscommercial.domain.models.Debt;
import reactor.core.publisher.Mono;

public interface IRestoreDebtUseCase {
    Mono<Debt> execute(String id, String organizationId);
}
