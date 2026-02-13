package pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.vgmscommercial.infrastructure.persistence.entity.PettyCashMovementEntity;
import reactor.core.publisher.Flux;

@Repository
public interface IPettyCashMovementR2dbcRepository extends R2dbcRepository<PettyCashMovementEntity, String> {

     Flux<PettyCashMovementEntity> findByPettyCashId(String pettyCashId);
}
