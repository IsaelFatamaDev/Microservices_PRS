package pe.edu.vallegrande.vgmscommercial.domain.ports.in.debt;

import pe.edu.vallegrande.vgmscommercial.domain.models.Debt;
import reactor.core.publisher.Mono;

public interface ICreateDebtUseCase {
    Mono<Debt> execute(Debt debt);
}