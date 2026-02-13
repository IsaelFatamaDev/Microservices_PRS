package pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.entity.ReceiptDetailEntity;
import reactor.core.publisher.Flux;

@Repository
public interface IReceiptDetailR2dbcRepository extends R2dbcRepository<ReceiptDetailEntity, String> {

     Flux<ReceiptDetailEntity> findByReceiptId(String receiptId);
}
