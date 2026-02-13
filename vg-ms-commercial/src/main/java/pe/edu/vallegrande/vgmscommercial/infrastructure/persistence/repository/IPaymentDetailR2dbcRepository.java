package pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.entity.PaymentDetailEntity;
import reactor.core.publisher.Flux;

@Repository
public interface IPaymentDetailR2dbcRepository extends R2dbcRepository<PaymentDetailEntity, String> {

     Flux<PaymentDetailEntity> findByPaymentId(String paymentId);
}
