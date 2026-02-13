package pe.edu.vallegrande.vgmscommercial.domain.ports.in.receipt;

import pe.edu.vallegrande.vgmscommercial.domain.models.Receipt;
import reactor.core.publisher.Mono;

public interface IRestoreReceiptUseCase {
    Mono<Receipt> execute(String id, String organizationId);
}